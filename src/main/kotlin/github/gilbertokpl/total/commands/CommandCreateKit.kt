package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.command.CommandSender

class CommandCreateKit : CommandManager("createkit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("criarkit"),
            active = MainConfig.kitsActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.createkit",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/createkit <kitName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val kitName = args[0]

        // --------------------------------------------------------
        // Validate kit name length
        // --------------------------------------------------------
        if (kitName.length > 16) {
            sender.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        // --------------------------------------------------------
        // Validate special characters
        // --------------------------------------------------------
        if (ServerUtil.hasSpecialCharacters(kitName)) {
            sender.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        // --------------------------------------------------------
        // Check if kit already exists
        // --------------------------------------------------------
        if (KitsData.checkIfExist(kitName)) {
            sender.sendMessage(LangConfig.kitsExist)
            return false
        }

        // --------------------------------------------------------
        // Create kit in cache and database
        // --------------------------------------------------------
        KitsData.createNewKitData(kitName)

        // --------------------------------------------------------
        // Notify player
        // --------------------------------------------------------
        sender.sendMessage(
            LangConfig.kitsCreateKitSuccess.replace("%kit%", kitName.lowercase())
        )

        // --------------------------------------------------------
        // Update kit inventory
        // --------------------------------------------------------
        Kit.setup()

        return false
    }
}
