package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.MainUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandCreateKit : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.kitsActivated,
            consoleCanUse = false,
            commandName = "createkit",
            timeCoolDown = null,
            permission = "essentialsk.commands.createkit",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf("/createkit <kitName>")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        //check if kit name do not contain special
        if (MainUtil.checkSpecialCaracteres(args[0])) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        val dataInstance = KitData[args[0].lowercase()]

        //check if exist
        if (dataInstance != null) {
            s.sendMessage(LangConfig.kitsExist)
            return false
        }

        //create cache and sql
        KitData.createNewKitData(args[0].lowercase())
        s.sendMessage(
            LangConfig.kitsCreateKitSuccess.replace(
                "%kit%",
                args[0].lowercase()
            )
        )
        return false
    }
}
