package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitDataDAO
import github.gilbertokpl.essentialsk.data.util.KitDataSQLUtil
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.MainUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandCreateKit : CommandCreator {
    override val active: Boolean = MainConfig.kitsActivated
    override val consoleCanUse = false
    override val permission = "essentialsk.commands.createkit"
    override val commandName = "createkit"
    override val timeCoolDown: Long? = null
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/createkit <kitName>")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.kitsNameLength)
            return false
        }

        //check if kit name do not contain special
        if (MainUtil.checkSpecialCaracteres(args[0])) {
            s.sendMessage(GeneralLang.generalSpecialCaracteresDisabled)
            return false
        }

        val dataInstance = KitDataDAO[args[0]]

        //check if exist
        if (dataInstance != null) {
            s.sendMessage(GeneralLang.kitsExist)
            return false
        }

        //create cache and sql
        s.sendMessage(GeneralLang.generalSendingInfoToDb)
        KitDataSQLUtil.createNewKitData(s, args[0].lowercase())
        return false
    }
}
