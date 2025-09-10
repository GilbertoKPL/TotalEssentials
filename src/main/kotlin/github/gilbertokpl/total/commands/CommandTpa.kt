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

class CommandTpa : CommandManager("tpa") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("back"),
            active = MainConfig.tpaActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/tpa <playerName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val targetName = args[0]

        // Não pode enviar TPA para si
        if (sender.name.equals(targetName, ignoreCase = true)) {
            sender.sendMessage(LangConfig.tpaSameName)
            return false
        }

        // Checa se o player destino está online
        val target = TotalEssentials.getInstance().server.getPlayer(targetName) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // Checa se já existe request enviado
        if (DataTeleport.checkTpa(sender)) {
            sender.sendMessage(LangConfig.tpaAlreadySend)
            return false
        }

        // Checa se target já tem request pendente
        if (DataTeleport.checkOtherTpa(target)) {
            sender.sendMessage(LangConfig.tpaAlreadyInAccept)
            return false
        }

        val timeToAccept = MainConfig.tpaTimeToAccept

        DataTeleport.createNewTpa(sender, target, timeToAccept)

        sender.sendMessage(LangConfig.tpaSuccess.replace("%player%", target.name))
        target.sendMessage(
            LangConfig.tpaOtherReceived
                .replace("%player%", sender.name)
                .replace("%time%", timeToAccept.toString())
        )

        return false
    }
}
