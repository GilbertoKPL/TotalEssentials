package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.chat.ChatManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandChat : CommandManager("chat") {

    override fun commandPattern() = CommandPattern(
        aliases = ChatManager.commandAliases(),
        active = ChatManager.isEnabled(),
        target = CommandTargetType.PLAYER,
        countdown = 0,
        permission = "",
        minimumSize = 0,
        maximumSize = null,
        usage = listOf("/chat", "/chat <canal> [mensagem]", "/chat join|leave <canal>", "/chat reload")
    )

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val player = sender as Player
        val aliasChannel = if (!label.equals("chat", true)) ChatManager.channelForAlias(label) else null

        if (aliasChannel != null) {
            if (args.isEmpty()) ChatManager.select(player, label)
            else ChatManager.handleChat(player, args.joinToString(" "), label)
            return false
        }

        if (args.isEmpty()) {
            ChatManager.list(player)
            return false
        }

        when (args[0].lowercase()) {
            "join", "entrar" -> {
                if (args.size < 2) return true
                if (!ChatManager.join(player, args[1])) player.sendMessage(ChatManager.message("channel-not-found"))
            }
            "leave", "sair" -> {
                if (args.size < 2) return true
                if (!ChatManager.leave(player, args[1])) player.sendMessage(ChatManager.message("channel-not-found"))
            }
            "reload", "recarregar" -> {
                if (!player.hasPermission("totalessentials.chat.reload")) {
                    player.sendMessage(ChatManager.message("no-permission"))
                    return false
                }
                if (ChatManager.reloadFromDisk()) {
                    player.sendMessage(ChatManager.message("reload"))
                }
            }
            else -> {
                if (args.size == 1) {
                    if (!ChatManager.select(player, args[0])) player.sendMessage(ChatManager.message("channel-not-found"))
                } else {
                    val channel = ChatManager.channelForAlias(args[0])
                    if (channel == null) player.sendMessage(ChatManager.message("channel-not-found"))
                    else ChatManager.handleChat(player, args.drop(1).joinToString(" "), args[0])
                }
            }
        }
        return false
    }
}
