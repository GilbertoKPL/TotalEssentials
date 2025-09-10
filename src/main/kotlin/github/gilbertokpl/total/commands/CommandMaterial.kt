package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMaterial : CommandManager("material") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("mat"),
            active = true,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.material",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/material",
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        sender.sendMessage(
            LangConfig.MaterialName.replace("%material%", (sender as Player).inventory.itemInHand.type.name).lowercase()
        )

        return false
    }
}