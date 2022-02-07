package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTrash : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.trashActivated,
            consoleCanUse = false,
            commandName = "trash",
            timeCoolDown = null,
            permission = "essentialsk.commands.trash",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/trash")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val inv =
            EssentialsK.instance.server.createInventory((s as Player), 36, LangConfig.trashMenuName)
        s.openInventory(inv)
        return false
    }
}
