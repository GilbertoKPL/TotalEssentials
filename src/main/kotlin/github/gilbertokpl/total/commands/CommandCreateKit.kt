package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.KitsData
import github.gilbertokpl.total.inventory.KitGuiInventory
import github.gilbertokpl.total.util.MainUtil
import org.bukkit.command.CommandSender

class CommandCreateKit : github.gilbertokpl.base.external.command.CommandCreator("createkit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("criarkit"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "essentialsk.commands.createkit",
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
        if (MainUtil.checkSpecialCaracteres(args[0])) {
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
