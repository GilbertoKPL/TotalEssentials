package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class CommandTrash : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "trash"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.trash"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/trash"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val inv =
            EssentialsK.instance.server.createInventory((s as Player), 36, GeneralLang.getInstance().trashMenuName)
        s.openInventory(inv)
        return false
    }
}