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
    val commandUsage: String
    val minimumSize: Int
    val maximumSize: Int

    fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        essentialsKExecutor(sender, command, label, args)
        return false
    }

    private fun essentialsKExecutor(
        s: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (s !is Player && !consoleCanUse) {
            s.sendMessage(GeneralLang.getInstance().generalOnlyPlayerCommand)
            return false
        }
        if (s is Player && !permission.isNullOrEmpty() && !s.hasPermission(permission!!)) {
            s.sendMessage(GeneralLang.getInstance().generalNotPerm)
            return false
        }
        TaskUtil.getInstance().commandExecutor {
            if (args.size > maximumSize || args.size < minimumSize) {
                s.sendMessage(GeneralLang.getInstance().generalCommandsUsage.replace("%command%", commandUsage))
                return@commandExecutor
            }
            kCommand(s, command, label, args)
        }
        return false
    }
}