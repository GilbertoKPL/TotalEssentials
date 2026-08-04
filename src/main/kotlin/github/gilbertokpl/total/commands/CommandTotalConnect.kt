package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.login.VelocityAuthBridge
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTotalConnect : CommandManager("totalconnect") {

    override fun commandPattern() = CommandPattern(
        aliases = emptyList(),
        active = true,
        target = CommandTargetType.PLAYER,
        countdown = 0,
        permission = "totalessentials.commands.totalconnect",
        minimumSize = 1,
        maximumSize = 1,
        usage = listOf("/totalconnect <servidor>")
    )

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val player = sender as Player
        val serverName = args[0].trim()
        if (serverName.length > 64 || !serverName.matches(Regex("[A-Za-z0-9_.-]+"))) {
            player.sendMessage(LangConfig.totalconnectInvalidName)
            return false
        }

        player.sendMessage(
            LangConfig.totalconnectTrying.replace("%server%", serverName)
        )
        VelocityAuthBridge.connectServer(player, serverName)
        return false
    }
}
