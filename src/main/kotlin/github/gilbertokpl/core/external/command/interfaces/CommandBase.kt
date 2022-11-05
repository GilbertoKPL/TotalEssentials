package github.gilbertokpl.core.external.command.interfaces

import github.gilbertokpl.core.external.command.annotations.CommandPattern
import org.bukkit.command.CommandSender

internal interface CommandBase {
    fun funCommand(
        s: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean

    fun commandPattern(): CommandPattern
}