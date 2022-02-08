package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandOnline : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.onlineActivated,
            consoleCanUse = true,
            commandName = "online",
            timeCoolDown = null,
            permission = "essentialsk.commands.online",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/online")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        s.sendMessage(
            LangConfig.onlineMessage.replace(
                "%amount%",
                PlayerUtil.getIntOnlinePlayers(MainConfig.onlineCountRemoveVanish)
                    .toString()
            )
        )
        return false
    }
}
