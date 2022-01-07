package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.sql.KitDataSQLUtil
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "delkit"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.delkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/delkit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val dataInstance = DataManager.getInstance().kitCacheV2[args[0].lowercase()]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(GeneralLang.getInstance().kitsNotExist)
            return false
        }

        //delete cache and sql
        s.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
        KitDataSQLUtil.getInstance().delKitData(s, args[0].lowercase())

        return false
    }
}
