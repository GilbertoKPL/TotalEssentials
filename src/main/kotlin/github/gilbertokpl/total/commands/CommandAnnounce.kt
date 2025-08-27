package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandCreator
import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
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

        // Determine sender name
        val name = if (s is Player) s.name else "Console"

        // Combine all arguments into a single message
        val rawMessage = args.joinToString(" ")

        // Apply color permissions if sender is a player
        val p = s as? Player
        val coloredMessage = PermissionUtil.colorPermission(p, rawMessage)

        // Format the final message
        val formattedMessage = LangConfig.announceSendAnnounce
            .replace("%name%", name)
            .replace("%message%", coloredMessage)

        // Send message to server
        MainUtil.serverMessage(formattedMessage)

        // Remove color codes and send to Discord
        val discordMessage = formattedMessage.replace(Regex("§[0-9a-fk-or]"), "")
        Discord.sendDiscordMessage(discordMessage, true)

        return false
    }
}

