package github.gilbertokpl.essentialsk.manager

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface ICommand : CommandExecutor {

    val permission: String?
    val consoleCanUse: Boolean
    val commandUsage: List<String>
    val minimumSize: Int
    val maximumSize: Int

    fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean

    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player && !consoleCanUse) {
            s.sendMessage(GeneralLang.getInstance().generalOnlyPlayerCommand)
            return false
        }
        if (s is Player && !permission.isNullOrEmpty() && !s.hasPermission(permission!!)) {
            s.sendMessage(GeneralLang.getInstance().generalNotPerm)
            return false
        }
        TaskUtil.getInstance().commandExecutor {
            if (args.size > maximumSize || args.size < minimumSize || kCommand(s, command, label, args)) {
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
                return@commandExecutor
            }
        }
        return false
    }
}