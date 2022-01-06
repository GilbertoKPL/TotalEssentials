package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.sql.KitDataSQLUtil
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "delkit"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.delkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/delkit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val dataInstance = DataManager.getInstance().kitCacheV2[args[0].lowercase()]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNotExist)
            return false
        }

        //delete cache and sql
        s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
        KitDataSQLUtil.getInstance().delKitData(s, args[0].lowercase())

        return false
    }
}