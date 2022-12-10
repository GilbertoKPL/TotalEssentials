package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.PlayerData
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandInvSee : github.gilbertokpl.core.external.command.CommandCreator("invsee") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.invseeActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.invsee",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf(
                "/invsee <playerName>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        //check if player is same
        if (args[0].equals(s.name.lowercase(), ignoreCase = true)) {
            s.sendMessage(LangConfig.invseeSameName)
            return false
        }

        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0])

        //check if player is online and not op
        if (p == null || p.isOp && !(s as Player).isOp || p.gameMode != GameMode.SURVIVAL) {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        PlayerData.inInvSee[s as Player] = p
        s.openInventory(p.inventory)

        return false
    }
}