package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTrash : github.gilbertokpl.base.external.command.CommandCreator("trash") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("lixo"),
            active = MainConfig.trashActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "essentialsk.commands.trash",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/trash")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val inv =
            github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(
                (s as Player),
                36,
                LangConfig.trashMenuName
            )
        s.openInventory(inv)
        return false
    }
}
