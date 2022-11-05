package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender

class CommandOnline : github.gilbertokpl.core.external.command.CommandCreator("online") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.onlineActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.online",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/online")
        )
    }


    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
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
