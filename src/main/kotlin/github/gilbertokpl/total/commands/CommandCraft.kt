package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandCraft : CommandManager("craft") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("craftar"),
            active = MainConfig.craftActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.craft",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/craft")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        (sender as Player).openWorkbench(sender.location, true)
        return false
    }
}
