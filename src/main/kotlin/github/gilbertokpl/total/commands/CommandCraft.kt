package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandCraft : github.gilbertokpl.core.external.command.CommandCreator("craft") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("craftar"),
            active = MainConfig.craftActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.craft",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/craft")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        (s as Player).openWorkbench(s.location, true)
        return false
    }
}
