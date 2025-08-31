package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender

class CommandDelKit : CommandCreator("delkit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("deletarkit"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.delkit",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/delkit <kitName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val kitName = args[0]

        // --------------------------------------------------------
        // Check if kit exists
        // --------------------------------------------------------
        if (!KitsData.checkIfExist(kitName)) {
            s.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        // --------------------------------------------------------
        // Delete kit from cache and database
        // --------------------------------------------------------
        KitsData.delete(kitName)

        // --------------------------------------------------------
        // Notify player
        // --------------------------------------------------------
        s.sendMessage(
            LangConfig.kitsDelKitSuccess.replace("%kit%", kitName.lowercase())
        )

        // --------------------------------------------------------
        // Update kit inventory
        // --------------------------------------------------------
        Kit.setup()

        return false
    }
}
