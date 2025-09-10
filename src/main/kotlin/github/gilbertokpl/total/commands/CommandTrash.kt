package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTrash : CommandManager("trash") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("lixo"),
            active = MainConfig.trashActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.trash",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/trash")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val inv =
            TotalEssentials.getInstance().server.createInventory(
                (sender as Player),
                36,
                LangConfig.trashMenuName
            )
        sender.openInventory(inv)
        return false
    }
}
