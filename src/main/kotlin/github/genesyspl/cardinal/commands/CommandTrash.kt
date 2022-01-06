package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class CommandTrash : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "trash"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.trash"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/trash"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val inv =
            github.genesyspl.cardinal.Cardinal.instance.server.createInventory(
                (s as Player),
                36,
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().trashMenuName
            )
        s.openInventory(inv)
        return false
    }
}