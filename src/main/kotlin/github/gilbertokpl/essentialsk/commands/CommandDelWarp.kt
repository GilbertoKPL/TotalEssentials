package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.WarpData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelWarp : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "delwarp"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.delwarp"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/delwarp <warpName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.getInstance().warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()
        val warpInstance = WarpData(warpName)

        //check if exist
        if (warpInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().warpsNameDontExist)
            return false
        }

        s.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
        warpInstance.delWarp(s)

        return false
    }
}