package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandCraft : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.craftActivated,
            consoleCanUse = false,
            commandName = "craft",
            timeCoolDown = null,
            permission = "essentialsk.commands.craft",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/craft")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        (s as Player).openWorkbench(s.location, true)
        return false
    }
}
