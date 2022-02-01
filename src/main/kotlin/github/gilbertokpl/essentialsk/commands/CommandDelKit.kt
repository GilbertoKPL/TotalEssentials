package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelKit : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.kitsActivated,
            consoleCanUse = true,
            commandName = "delkit",
            timeCoolDown = null,
            permission = "essentialsk.commands.delkit",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf("/delkit <kitName>")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val dataInstance = KitData[args[0]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(GeneralLang.kitsNotExist)
            return false
        }

        KitData.delKitData(args[0].lowercase())
        s.sendMessage(GeneralLang.kitsDelKitSuccess.replace("%kit%", args[0].lowercase()))

        return false
    }
}
