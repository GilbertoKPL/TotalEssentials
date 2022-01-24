package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.WarpDataDAO
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelWarp : CommandCreator {
    override val active: Boolean = MainConfig.warpsActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "delwarp"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.delwarp"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/delwarp <warpName>")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.warpsNameLength)
            return false
        }

        WarpDataDAO[args[0]] ?: run {
            s.sendMessage(GeneralLang.warpsNameDontExist)
            return false
        }

        s.sendMessage(GeneralLang.generalSendingInfoToDb)
        WarpDataDAO.del(args[0])

        return false
    }
}
