package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.chat.ChatManager
import github.gilbertokpl.total.config.files.ChatConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTell : CommandManager("tell") {
    override fun commandPattern() = CommandPattern(
        aliases = ChatConfig.tellAliases,
        active = ChatConfig.tellActivated,
        target = CommandTargetType.PLAYER,
        countdown = 0,
        permission = ChatConfig.tellPermission,
        minimumSize = 2,
        maximumSize = null,
        usage = listOf("/tell <jogador> <mensagem>")
    )

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val player = sender as Player
        ChatManager.sendPrivateMessage(player, args[0], args.drop(1).joinToString(" "), false)
        return false
    }
}
