package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerData
import github.gilbertokpl.essentialsk.data.dao.WarpData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : CommandCreator {
    override val active: Boolean = MainConfig.warpsActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "warp"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.warp"
    override val minimumSize = 0
    override val maximumSize = 2
    override val commandUsage =
        listOf(
            "P_/warp <warpName>",
            "essentialsk.commands.warp.other_/warp <playerName> <warpName>"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = if (s is Player) {
            s
        } else {
            null
        }

        if (args.isEmpty()) {
            s.sendMessage(
                GeneralLang.warpsWarpList.replace(
                    "%list%",
                    WarpData.getList(p).toString()
                )
            )
            return false
        }

        if (p == null || (args.size == 2 && p.hasPermission("essentialsk.commands.warp.other"))) {
            val newPlayer = Bukkit.getPlayer(args[0].lowercase()) ?: return true

            val warpName = args[1].lowercase()

            val warpInstance = WarpData[warpName]

            //check if not exist
            if (warpInstance == null) {
                s.sendMessage(
                    GeneralLang.warpsWarpList.replace(
                        "%list%",
                        WarpData.getList(null).toString()
                    )
                )
                return false
            }

            newPlayer.teleport(warpInstance)

            newPlayer.sendMessage(GeneralLang.warpsTeleportedOtherMessage.replace("%warp%", warpName))
            s.sendMessage(
                GeneralLang.warpsTeleportedOtherSuccess
                    .replace("%warp%", warpName)
                    .replace("%player%", newPlayer.name.lowercase())
            )

            return false
        }

        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()

        val warpInstance = WarpData[warpName] ?: run {
            p.sendMessage(
                GeneralLang.warpsWarpList.replace(
                    "%list%",
                    WarpData.getList(p).toString()
                )
            )
            return false
        }

        val playerCache = PlayerData[p] ?: return false

        if (!p.hasPermission("essentialsk.commands.warp.$warpName")) {
            p.sendMessage(GeneralLang.generalNotPerm)
            return false
        }

        if (p.hasPermission("essentialsk.bypass.teleport")) {
            p.teleport(warpInstance)
            p.sendMessage(GeneralLang.warpsTeleported.replace("%warp%", warpName))
            return false
        }

        if (playerCache.inTeleport) {
            p.sendMessage(GeneralLang.warpsInTeleport)
            return false
        }

        val time = MainConfig.warpsTimeToTeleport

        val exe = TaskUtil.teleportExecutor(time)

        playerCache.inTeleport = true

        exe {
            playerCache.inTeleport = false
            p.teleport(warpInstance)
            s.sendMessage(GeneralLang.warpsTeleported.replace("%warp%", warpName))
        }

        s.sendMessage(
            GeneralLang.warpsSendTimeToTeleport.replace("%warp%", warpName)
                .replace("%time%", time.toString())
        )

        return false
    }
}
