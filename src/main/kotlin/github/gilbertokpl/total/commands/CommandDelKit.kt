package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.internal.KitGuiInventory
import org.bukkit.command.CommandSender

class CommandDelKit : github.gilbertokpl.core.external.command.CommandCreator("delkit") {

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

        //check if not exist
        if (!KitsData.checkIfExist(args[0])) {
            s.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        //delete kit
        KitsData.delete(args[0])

        //send message
        s.sendMessage(
            LangConfig.kitsDelKitSuccess.replace(
                "%kit%",
                args[0].lowercase()
            )
        )

        //update inventory

        KitGuiInventory.setup()

        return false
    }
}
