package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.data.dao.WarpData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.warpsActivated,
            consoleCanUse = true,
            commandName = "warp",
            timeCoolDown = null,
            permission = "essentialsk.commands.warp",
            minimumSize = 0,
            maximumSize = 2,
            commandUsage = listOf(
                "P_/warp <warpName>",
                "essentialsk.commands.warp.other_/warp <playerName> <warpName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = if (s is Player) {
            s
        } else {
            null
        }

        if (args.isEmpty()) {
            s.sendMessage(
                LangConfig.warpsWarpList.replace(
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
                    LangConfig.warpsWarpList.replace(
                        "%list%",
                        WarpData.getList(null).toString()
                    )
                )
                return false
            }

            newPlayer.teleport(warpInstance)

            newPlayer.sendMessage(LangConfig.warpsTeleportedOtherMessage.replace("%warp%", warpName))
            s.sendMessage(
                LangConfig.warpsTeleportedOtherSuccess
                    .replace("%warp%", warpName)
                    .replace("%player%", newPlayer.name.lowercase())
            )

            return false
        }

        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()

        val warpInstance = WarpData[warpName] ?: run {
            p.sendMessage(
                LangConfig.warpsWarpList.replace(
                    "%list%",
                    WarpData.getList(p).toString()
                )
            )
            return false
        }

        val playerCache = PlayerData[p] ?: return false

        if (!p.hasPermission("essentialsk.commands.warp.$warpName")) {
            p.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        val time = MainConfig.warpsTimeToTeleport

        if (p.hasPermission("essentialsk.bypass.teleport") || time == 0) {
            p.teleport(warpInstance)
            p.sendMessage(LangConfig.warpsTeleported.replace("%warp%", warpName))
            return false
        }

        if (playerCache.inTeleport) {
            p.sendMessage(LangConfig.warpsInTeleport)
            return false
        }

        val exe = TaskUtil.teleportExecutor(time)

        playerCache.inTeleport = true

        exe {
            playerCache.inTeleport = false
            p.teleport(warpInstance)
            s.sendMessage(LangConfig.warpsTeleported.replace("%warp%", warpName))
        }

        s.sendMessage(
            LangConfig.warpsSendTimeToTeleport.replace("%warp%", warpName)
                .replace("%time%", time.toString())
        )

        return false
    }
}
