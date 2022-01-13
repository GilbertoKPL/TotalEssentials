package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.WarpDataV2
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : CommandCreator {
    override val active: Boolean = MainConfig.warpsActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "warp"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.warp"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage =
        listOf(
            "/warp <warpName>",
            "essentialsk.commands.warp.other_/warp <playerName> <warpName>"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = if (s is Player) {
            s
        } else {
            null
        }

        if (args.isEmpty() || p == null) {
            s.sendMessage(
                GeneralLang.warpsWarpList.replace(
                    "%list%",
                    WarpDataV2.getList(p).toString()
                )
            )
            return false
        }

        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()

        val warpInstance = WarpDataV2[warpName]

        //check if not exist
        if (warpInstance == null) {
            s.sendMessage(
                GeneralLang.warpsWarpList.replace(
                    "%list%",
                    WarpDataV2.getList(p).toString()
                )
            )
            return false
        }

        if (!s.hasPermission("essentialsk.commands.warp.$warpName")) {
            s.sendMessage(GeneralLang.generalNotPerm)
            return false
        }

        if (s.hasPermission("essentialsk.bypass.teleport")) {
            p.teleport(warpInstance)
            s.sendMessage(GeneralLang.warpsTeleported.replace("%warp%", warpName))
            return false
        }

        val time = MainConfig.warpsTimeToTeleport

        val exe = TaskUtil.teleportExecutor(time)

        DataManager.inTeleport.add(p)

        exe {
            DataManager.inTeleport.remove(s)
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
