package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandCreateKit : ICommand {
    override val consoleCanUse = false
    override val permission = "essentialsk.commands.createkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/createkit (kitName)")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.getInstance().kitsNameLength)
            return false
        }

        //check if kit name do not contain . or - to not bug
        if (PluginUtil.getInstance().checkSpecialCaracteres(args[0])) {
            s.sendMessage(GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        val dataInstance = KitData(args[0])

        //check if exist
        if (dataInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().kitsExist)
            return false
        }

        //create cache and sql
        dataInstance.createNewKitData()
        s.sendMessage(
            GeneralLang.getInstance().kitsCreateKitSuccess.replace(
                "%kit%",
                args[0]
            )
        )
        return false
    }
}