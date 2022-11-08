package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTphere : github.gilbertokpl.core.external.command.CommandCreator("tphere") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("puxar"),
            active = MainConfig.tphereActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tphere",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/tphere <playerName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        // check if player is online
        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        p.teleport((s as Player).location)

        p.sendMessage(
            LangConfig.tphereTeleportedOtherSuccess
        )
        s.sendMessage(
            LangConfig.tphereTeleportedSuccess.replace("%player%", p.name)
        )
        return false
    }
}
