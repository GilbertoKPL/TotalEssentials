package github.gilbertokpl.essentialsk.manager

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TimeUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface ICommand : CommandExecutor {

    val timeCoolDown: Long?
    val commandName: String
    val permission: String?
    val consoleCanUse: Boolean
    val commandUsage: List<String>
    val minimumSize: Int?
    val maximumSize: Int?

    fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean

    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player && !consoleCanUse) {
            s.sendMessage(GeneralLang.getInstance().generalOnlyPlayerCommand)
            return true
        }
        if (s is Player && !permission.isNullOrEmpty() && !s.hasPermission(permission!!)) {
            s.sendMessage(GeneralLang.getInstance().generalNotPerm)
            return true
        }
        fun errorMessage() {
            s.sendMessage(GeneralLang.getInstance().generalCommandsUsage)
            for (it in commandUsage) {
                val to = it.split("_")
                if (to.size == 1) {
                    s.sendMessage(GeneralLang.getInstance().generalCommandsUsageList.replace("%command%", it))
                    continue
                }
                if (to[0] == "C" && s is Player || to[0] == "P" && s !is Player) {
                    continue
                }
                if (s !is Player || s.hasPermission(to[0])) {
                    s.sendMessage(GeneralLang.getInstance().generalCommandsUsageList.replace("%command%", to[1]))
                }
            }
        }
        try {
            if (maximumSize != null && args.size > maximumSize!! || minimumSize != null && args.size < minimumSize!!) {
                errorMessage()
                return true
            }
            if (timeCoolDown != null && s is Player && !s.hasPermission("essentialsk.bypass.waitcommand")) {
                val playerData = DataManager.getInstance().playerCacheV2[s.name.lowercase()]!!
                val time = playerData.getCoolDown(commandName)
                if (time != 0L && System.currentTimeMillis() < time) {
                    s.sendMessage(
                        GeneralLang.getInstance().generalCooldownMoreTime.replace(
                            "%time%",
                            TimeUtil.getInstance().convertMillisToString(time - System.currentTimeMillis(), true)
                        )
                    )
                    return true
                }
                if (kCommand(s, command, label, args)) {
                    errorMessage()
                    return true
                }
                playerData.setCoolDown(
                    commandName,
                    System.currentTimeMillis() + (timeCoolDown!! * 1000)
                )
                return true
            }
            if (kCommand(s, command, label, args)) {
                errorMessage()
                return true
            }
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
        }
        return true
    }
}