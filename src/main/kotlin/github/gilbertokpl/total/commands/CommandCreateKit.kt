package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.internal.KitGuiInventory
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MainUtil
import org.bukkit.command.CommandSender

class CommandCreateKit : github.gilbertokpl.core.external.command.CommandCreator("createkit") {

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
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        //check if kit name do not contain special
        if (MainUtil.checkSpecialCharacters(args[0])) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        //check if exist
        if (KitsData.checkIfExist(args[0])) {
            s.sendMessage(LangConfig.kitsExist)
            return false
        }

        //create cache and sql
        KitsData.createNewKitData(args[0])

        //send message
        s.sendMessage(
            LangConfig.kitsCreateKitSuccess.replace(
                "%kit%",
                args[0].lowercase()
            )
        )

        //update inventoy
        KitGuiInventory.setup()


        return false
    }
}
