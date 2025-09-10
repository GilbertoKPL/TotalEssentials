package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.WorldUtil
import org.bukkit.command.CommandSender

class CommandClearEntities : CommandManager("clearentities") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("limparchao"),
            active = MainConfig.clearentitiesActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.clearentities",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/limparchao",
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        WorldUtil.clearEntities()

        return false
    }
}
