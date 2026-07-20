package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.chat.ChatManager
import github.gilbertokpl.total.config.files.ChatConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandReply : CommandManager("reply") {
    override fun commandPattern() = CommandPattern(
        aliases = ChatConfig.tellReplyAliases,
        active = ChatConfig.tellActivated,
        target = CommandTargetType.PLAYER,
        countdown = 0,
        permission = ChatConfig.tellReplyPermission,
        minimumSize = 1,
        maximumSize = null,
        usage = listOf("/r <mensagem>")
    )

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        ChatManager.sendPrivateMessage(sender as Player, null, args.joinToString(" "), true)
        return false
    }
}
