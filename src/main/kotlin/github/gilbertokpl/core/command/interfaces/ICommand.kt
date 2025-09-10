package github.gilbertokpl.core.command.interfaces

import github.gilbertokpl.core.command.pattern.CommandPattern
import org.bukkit.command.CommandSender

internal interface ICommand {

    fun funCommand(
        sender: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean

    fun commandPattern(): CommandPattern
}