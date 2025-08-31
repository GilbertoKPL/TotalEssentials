package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.WorldUtil
import org.bukkit.command.CommandSender

class CommandClearEntities : CommandCreator("clearentities") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("limparchao"),
            active = MainConfig.clearentitiesActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.clearentities",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/limparchao",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        WorldUtil.clearEntities()

        return false
    }
}
