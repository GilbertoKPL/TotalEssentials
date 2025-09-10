package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.inventory.EditKit.editKitGui
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEditKit : CommandManager("editkit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("editarkit"),
            active = MainConfig.kitsActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.editkit",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/editkit <kitName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val kitName = args[0].lowercase()
        val player = sender as Player

        // --------------------------------------------------------
        // Check length of kit name
        // --------------------------------------------------------
        if (kitName.length > 16) {
            player.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        // --------------------------------------------------------
        // Check if kit exists
        // --------------------------------------------------------
        if (!KitsData.checkIfExist(kitName)) {
            player.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        // --------------------------------------------------------
        // Open edit kit GUI
        // --------------------------------------------------------
        editKitGui(player, kitName)
        return false
    }
}
