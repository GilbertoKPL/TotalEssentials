package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.WarpData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

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

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        Dao.getInstance().inTeleport.add(s)

        exe {
            Dao.getInstance().inTeleport.remove(s)
            s.teleport(warpInstance.getLocation())
            s.sendMessage(GeneralLang.getInstance().warpsTeleported.replace("%warp%", warpName))
        }

        s.sendMessage(
            GeneralLang.getInstance().warpsSendTimeToTeleport.replace("%warp%", warpName).replace("%time%", time.toString())
        )

        return false
    }
}