package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataTeleport
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpa : CommandCreator("tpa") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("back"),
            active = MainConfig.tpaActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/tpa <playerName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        if (s !is Player) return false

        val targetName = args[0]

        // Não pode enviar TPA para si mesmo
        if (s.name.equals(targetName, ignoreCase = true)) {
            s.sendMessage(LangConfig.tpaSameName)
            return false
        }

        // Checa se o player destino está online
        val target = TotalEssentials.getInstance().server.getPlayer(targetName) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // Checa se já existe request enviado
        if (DataTeleport.checkTpa(s)) {
            s.sendMessage(LangConfig.tpaAlreadySend)
            return false
        }

        // Checa se target já tem request pendente
        if (DataTeleport.checkOtherTpa(target)) {
            s.sendMessage(LangConfig.tpaAlreadyInAccept)
            return false
        }

        val timeToAccept = MainConfig.tpaTimeToAccept

        DataTeleport.createNewTpa(s, target, timeToAccept)

        s.sendMessage(LangConfig.tpaSuccess.replace("%player%", target.name))
        target.sendMessage(
            LangConfig.tpaOtherReceived
                .replace("%player%", s.name)
                .replace("%time%", timeToAccept.toString())
        )

        return false
    }
}
