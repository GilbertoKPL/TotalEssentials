package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.delkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/delkit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val dataInstance = KitData(args[0])

        //check if not exist
        if (!dataInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().kitsNotExist)
            return false
        }

        //get fakeName to send
        val fakeName = dataInstance.getCache().fakeName

        //delete cache and sql
        s.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
        if (dataInstance.delKitData()) {
            s.sendMessage(GeneralLang.getInstance().kitsDelKitSuccess.replace("%kit%", fakeName))
        }

        return false
    }
}