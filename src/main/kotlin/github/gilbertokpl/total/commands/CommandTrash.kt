package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTrash : CommandCreator("trash") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("lixo"),
            active = MainConfig.trashActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.trash",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/trash")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val inv =
            TotalEssentials.getInstance().server.createInventory(
                (s as Player),
                36,
                LangConfig.trashMenuName
            )
        s.openInventory(inv)
        return false
    }
}
