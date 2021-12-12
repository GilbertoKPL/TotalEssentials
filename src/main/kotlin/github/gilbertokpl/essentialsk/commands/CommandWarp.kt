package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.WarpData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.warp"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage =
        listOf("/warp <warpName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s is Player) {
            s.sendMessage(
                GeneralLang.getInstance().warpsWarpList.replace(
                    "%list%",
                    WarpData("").getWarpList(s).toString()
                )
            )
            return false
        }

        if (s !is Player) {
            s.sendMessage(
                GeneralLang.getInstance().warpsWarpList.replace(
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
            s.sendMessage(GeneralLang.getInstance().warpsNameLength)
            return false
        }

        //check if not exist
        if (warpInstance.checkCache()) {
            s.sendMessage(
                GeneralLang.getInstance().warpsWarpList.replace(
                    "%list%",
                    warpInstance.getWarpList(null).toString()
                )
            )
            return false
        }

        if (s.hasPermission("essentialsk.bypass.teleport")) {
            s.teleport(warpInstance.getLocation())
            s.sendMessage(GeneralLang.getInstance().warpsTeleported.replace("%warp%", warpName))
            return false
        }

        val time = MainConfig.getInstance().warpsTimeToTeleport

        Dao.getInstance().inTeleport.add(s)

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        exe {
            Dao.getInstance().inTeleport.remove(s)
            s.teleport(warpInstance.getLocation())
            s.sendMessage(GeneralLang.getInstance().warpsTeleported.replace("%warp%", warpName))
        }

        s.sendMessage(
            GeneralLang.getInstance().warpsTimeToTeleport.replace("%warp%", warpName).replace("%time%", time.toString())
        )

        return false
    }
}