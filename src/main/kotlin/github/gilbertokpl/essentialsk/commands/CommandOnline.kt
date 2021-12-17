package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandOnline : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.online"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/online")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        return false
    }
}