package github.gilbertokpl.essentialsk.manager

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.CoolDownCache.getCoolDown
import github.gilbertokpl.essentialsk.player.modify.CoolDownCache.setCoolDown
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TimeUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal interface CommandCreator : CommandExecutor {

    val commandData: CommandData


    fun funCommand(
        s: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean

    override fun onCommand(
        s: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (s !is Player && !commandData.consoleCanUse) {
            s.sendMessage(LangConfig.generalOnlyPlayerCommand)
            return true
        }
        if (s is Player && !commandData.permission.isNullOrEmpty() && !s.hasPermission(commandData.permission!!)) {
            s.sendMessage(LangConfig.generalNotPerm)
            return true
        }
        fun errorMessage() {
            s.sendMessage(LangConfig.generalCommandsUsage)
            for (it in commandData.commandUsage) {
                val to = it.split("_")
                if (to.size == 1) {
                    s.sendMessage(LangConfig.generalCommandsUsageList.replace("%command%", it))
                    continue
                }
                if (to[0] == "C" && s is Player || to[0] == "P" && s !is Player) {
                    continue
                }
                if (s !is Player || s.hasPermission(to[0])) {
                    s.sendMessage(LangConfig.generalCommandsUsageList.replace("%command%", to[1]))
                }
            }
        }
        try {
            if (commandData.maximumSize != null && args.size > commandData.maximumSize!! || commandData.minimumSize != null && args.size < commandData.minimumSize!!) {
                errorMessage()
                return true
            }
            if (commandData.timeCoolDown != null && s is Player && !s.hasPermission("essentialsk.bypass.waitcommand")) {
                val playerData = PlayerData[s] ?: return false
                val time = playerData.getCoolDown(commandData.commandName)
                if (time != 0L && System.currentTimeMillis() < time) {
                    s.sendMessage(
                        LangConfig.generalCooldownMoreTime.replace(
                            "%time%",
                            TimeUtil.convertMillisToString(time - System.currentTimeMillis(), true)
                        )
                    )
                    return true
                }
                if (funCommand(s, command, label, args)) {
                    errorMessage()
                    return true
                }
                playerData.setCoolDown(
                    commandData.commandName,
                    System.currentTimeMillis() + (commandData.timeCoolDown!! * 1000)
                )
                return true
            }
            if (funCommand(s, command, label, args)) {
                errorMessage()
                return true
            }
        } catch (ex: Exception) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
        }
        return true
    }
}
