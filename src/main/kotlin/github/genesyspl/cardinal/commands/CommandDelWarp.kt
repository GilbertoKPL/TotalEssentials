package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.`object`.WarpData
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelWarp : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "delwarp"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.delwarp"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/delwarp <warpName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()
        val warpInstance = WarpData(warpName)

        //check if exist
        if (warpInstance.checkCache()) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsNameDontExist)
            return false
        }

        s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
        warpInstance.delWarp(s)

        return false
    }
}