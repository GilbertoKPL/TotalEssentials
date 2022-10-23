package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.KitsData
import github.gilbertokpl.total.inventory.EditKitInventory.editKitGui
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEditKit : github.gilbertokpl.base.external.command.CommandCreator("editkit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("editarkit"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "essentialsk.commands.editkit",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/editkit <kitName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        //check if not exist
        if (!KitsData.checkIfExist(args[0])) {
            s.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        //open inventory
        editKitGui(s as Player, args[0].lowercase())
        return false
    }
}
