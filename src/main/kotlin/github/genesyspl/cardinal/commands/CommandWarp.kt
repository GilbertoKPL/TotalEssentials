package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.WarpData
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "warp"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.warp"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage =
        listOf("/warp <warpName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s is Player) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsWarpList.replace(
                    "%list%",
                    WarpData("").getWarpList(s).toString()
                )
            )
            return false
        }

        if (s !is Player) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsWarpList.replace(
                    "%list%",
                    WarpData("").getWarpList(null).toString()
                )
            )
            return false
        }

        val warpName = args[0].lowercase()

        val warpInstance = WarpData(warpName)

        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsNameLength)
            return false
        }

        //check if not exist
        if (warpInstance.checkCache()) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsWarpList.replace(
                    "%list%",
                    warpInstance.getWarpList(s).toString()
                )
            )
            return false
        }

        if (!s.hasPermission("cardinal.commands.warp.$warpName")) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
            return false
        }

        if (s.hasPermission("cardinal.bypass.teleport")) {
            s.teleport(warpInstance.getLocation())
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsTeleported.replace(
                    "%warp%",
                    warpName
                )
            )
            return false
        }

        val time = github.genesyspl.cardinal.configs.MainConfig.getInstance().warpsTimeToTeleport

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        DataManager.getInstance().inTeleport.add(s)

        exe {
            DataManager.getInstance().inTeleport.remove(s)
            s.teleport(warpInstance.getLocation())
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsTeleported.replace(
                    "%warp%",
                    warpName
                )
            )
        }

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsSendTimeToTeleport.replace(
                "%warp%",
                warpName
            )
                .replace("%time%", time.toString())
        )

        return false
    }
}