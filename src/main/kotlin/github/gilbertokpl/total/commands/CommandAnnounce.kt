package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import github.gilbertokpl.total.util.ServerUtil
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAnnounce : CommandManager("announce") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("anunciar"),
            active = MainConfig.announceActivated,
            target = CommandTargetType.ALL,
            countdown = MainConfig.announceCooldown.toLong(),
            permission = "totalessentials.commands.announce",
            minimumSize = 1,
            maximumSize = null,
            usage = listOf("/announce <msg>", "/anunciar <msg>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // Determine sender name
        val name = if (sender is Player) sender.name else "Console"

        // Combine all arguments into a single message
        val rawMessage = args.joinToString(" ")

        // Apply color permissions if sender is a player
        val p = sender as? Player
        val coloredMessage = PermissionUtil.colorPermission(p, rawMessage)

        // Format the final message
        val formattedMessage = LangConfig.announceSendAnnounce
            .replace("%name%", name)
            .replace("%message%", coloredMessage)

        // Send message to server
        ServerUtil.serverMessage(formattedMessage)

        // Remove color codes and send to Discord
        val discordMessage = formattedMessage.replace(Regex("§[0-9a-fk-or]"), "")
        DiscordManager.sendDiscordMessage(discordMessage, true)

        return false
    }
}

