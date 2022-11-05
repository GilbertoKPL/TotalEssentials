package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandCreator
import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MainUtil
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAnnounce : CommandCreator("announce") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("anunciar"),
            active = MainConfig.announceActivated,
            target = CommandTarget.ALL,
            countdown = MainConfig.announceCooldown.toLong(),
            permission = "totalessentials.commands.announce",
            minimumSize = 1,
            maximumSize = null,
            usage = listOf("/announce <msg>", "/anunciar <msg>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val name = if (s !is Player) {
            "Console"
        } else {
            s.name
        }

        val msg = StringBuilder()
        for (arg in args) {
            msg.append(arg).append(" ")
        }

        val p = if (s is Player) {
            s
        } else {
            null
        }

        val newMessage = PermissionUtil.colorPermission(p, msg.toString())

        MainUtil.serverMessage(
            LangConfig.announceSendAnnounce
                .replace("%name%", name)
                .replace("%message%", newMessage)
        )

        return false
    }

}
