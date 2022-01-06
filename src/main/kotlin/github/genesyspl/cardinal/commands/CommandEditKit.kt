package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.inventory.EditKitInventory.editKitGui
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEditKit : ICommand {

    override val consoleCanUse: Boolean = false
    override val commandName = "editkit"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.editkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/editkit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNameLength)
            return false
        }

        val dataInstance = DataManager.getInstance().kitCacheV2[args[0].lowercase()]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNotExist)
            return false
        }

        //open inventory
        editKitGui(s as Player, args[0].lowercase())
        return false
    }
}