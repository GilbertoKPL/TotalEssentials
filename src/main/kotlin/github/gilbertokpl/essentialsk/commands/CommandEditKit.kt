package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.inventory.EditKitInventory.editKitGui
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEditKit : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.kitsActivated,
            consoleCanUse = false,
            commandName = "editkit",
            timeCoolDown = null,
            permission = "essentialsk.commands.editkit",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf("/editkit <kitName>")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.kitsNameLength)
            return false
        }

        val dataInstance = KitData[args[0]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(GeneralLang.kitsNotExist)
            return false
        }

        //open inventory
        editKitGui(s as Player, args[0].lowercase())
        return false
    }
}
