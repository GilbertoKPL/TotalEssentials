package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender

class CommandOnline : CommandManager("online") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.onlineActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.online",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/online")
        )
    }


    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(
            LangConfig.onlineMessage.replace(
                "%amount%",
                PlayerUtil.getOnlinePlayersCount(MainConfig.onlineCountRemoveVanish)
                    .toString()
            )
        )
        return false
    }
}
