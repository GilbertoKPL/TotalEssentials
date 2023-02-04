package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.internal.TpaData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpa : github.gilbertokpl.core.external.command.CommandCreator("tpa") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("back"),
            active = MainConfig.tpaActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf(
                "/tpa <playerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        //check if player is same
        if (args[0].lowercase() == s.name.lowercase()) {
            s.sendMessage(LangConfig.tpaSameName)
            return false
        }

        //check if player is online
        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        //check if player already send
        if (TpaData.checkTpa(s as Player)) {
            s.sendMessage(LangConfig.tpaAlreadySend)
            return false
        }

        //check if player has telepot request
        if (TpaData.checkOtherTpa(p)) {
            s.sendMessage(LangConfig.tpaAlreadyInAccept)
            return false
        }
        val time = MainConfig.tpaTimeToAccept

        TpaData.createNewTpa(s, p, time)

        s.sendMessage(LangConfig.tpaSuccess.replace("%player%", p.name))
        p.sendMessage(
            LangConfig.tpaOtherReceived.replace("%player%", s.name)
                .replace("%time%", time.toString())
        )
        return false
    }
}
