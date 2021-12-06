package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.delkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = "/delkit (kitName)"

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>) {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.getInstance().kitsNameLength)
            return
        }

        val dataInstance = KitData(args[0])

        //check if not exist
        if (!dataInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().kitsNotExist)
            return
        }

        //get fakeName to send
        val fakeName = dataInstance.getCache().fakeName

        //delete cache and sql
        dataInstance.delKitData()
        s.sendMessage(GeneralLang.getInstance().kitsDelKitSuccess.replace("%kit%", fakeName))

    }
}