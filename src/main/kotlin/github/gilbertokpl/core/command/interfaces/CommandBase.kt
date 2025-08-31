package github.gilbertokpl.core.command.interfaces

import github.gilbertokpl.core.command.annotations.CommandPattern
import org.bukkit.command.CommandSender

internal interface CommandBase {
    fun funCommand(
        s: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean

    fun commandPattern(): CommandPattern
}