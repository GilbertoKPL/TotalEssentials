package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataTeleport
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpdeny : CommandManager("tpdeny") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(),
            active = MainConfig.tpaActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/tpdeny")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        val player = sender

        val tpaSender = DataTeleport.getTpa(player) ?: run {
            player.sendMessage(LangConfig.tpaNotAnyRequestToDeny)
            return false
        }

        // Remove o pedido de teleporte
        DataTeleport.remove(tpaSender)

        player.sendMessage(LangConfig.tpaRequestDeny.replace("%player%", tpaSender.name))

        // Verifica se o outro jogador ainda está online antes de enviar mensagem
        TotalEssentials.getInstance().server.getPlayer(tpaSender.name)?.let { otherPlayer ->
            otherPlayer.sendMessage(
                LangConfig.tpaRequestOtherDeny.replace("%player%", player.name)
            )
        }

        return false
    }
}
