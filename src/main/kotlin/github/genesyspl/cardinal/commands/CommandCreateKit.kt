package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.sql.KitDataSQLUtil
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.PluginUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandCreateKit : ICommand {
    override val consoleCanUse = false
    override val permission = "cardinal.commands.createkit"
    override val commandName = "createkit"
    override val timeCoolDown: Long? = null
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/createkit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNameLength)
            return false
        }

        //check if kit name do not contain special
        if (PluginUtil.getInstance().checkSpecialCaracteres(args[0])) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        val dataInstance = DataManager.getInstance().kitCacheV2[args[0].lowercase()]

        //check if exist
        if (dataInstance != null) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsExist)
            return false
        }

        //create cache and sql
        s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
        KitDataSQLUtil.getInstance().createNewKitData(s, args[0].lowercase())
        return false
    }
}