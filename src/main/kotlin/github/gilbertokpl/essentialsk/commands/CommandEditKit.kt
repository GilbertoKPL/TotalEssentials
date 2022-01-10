package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.inventory.EditKitInventory.editKitGui
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEditKit : CommandCreator {

    override val consoleCanUse: Boolean = false
    override val commandName = "editkit"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.editkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/editkit <kitName>")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.kitsNameLength)
            return false
        }

        val dataInstance = KitDataV2[args[0]]

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
