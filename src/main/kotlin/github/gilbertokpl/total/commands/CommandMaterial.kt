package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMaterial : github.gilbertokpl.core.external.command.CommandCreator("material") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("mat"),
            active = true,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.material",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/material",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        s.sendMessage(
            LangConfig.MaterialName.replace("%material%", (s as Player).inventory.itemInHand.type.name).lowercase()
        )

        return false
    }
}