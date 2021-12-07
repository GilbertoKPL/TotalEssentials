package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandSetHome : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.nick"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage = listOf("/sethome (homeName)")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>) : Boolean {

        return false
    }
}