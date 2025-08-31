package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.command.CommandSender

class CommandCreateKit : CommandCreator("createkit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("criarkit"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.createkit",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/createkit <kitName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val kitName = args[0]

        // --------------------------------------------------------
        // Validate kit name length
        // --------------------------------------------------------
        if (kitName.length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        // --------------------------------------------------------
        // Validate special characters
        // --------------------------------------------------------
        if (ServerUtil.checkSpecialCharacters(kitName)) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        // --------------------------------------------------------
        // Check if kit already exists
        // --------------------------------------------------------
        if (KitsData.checkIfExist(kitName)) {
            s.sendMessage(LangConfig.kitsExist)
            return false
        }

        // --------------------------------------------------------
        // Create kit in cache and database
        // --------------------------------------------------------
        KitsData.createNewKitData(kitName)

        // --------------------------------------------------------
        // Notify player
        // --------------------------------------------------------
        s.sendMessage(
            LangConfig.kitsCreateKitSuccess.replace("%kit%", kitName.lowercase())
        )

        // --------------------------------------------------------
        // Update kit inventory
        // --------------------------------------------------------
        Kit.setup()

        return false
    }
}
