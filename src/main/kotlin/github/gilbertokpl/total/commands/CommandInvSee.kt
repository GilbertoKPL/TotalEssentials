package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandInvSee : CommandCreator("invsee") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.invseeActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.invsee",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/invsee <playerName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // prevent seeing own inventory
        if (args[0].equals(s.name.lowercase(), ignoreCase = true)) {
            s.sendMessage(LangConfig.invseeSameName)
            return false
        }

        // get target player
        val target = TotalEssentials.getInstance().server.getPlayer(args[0])

        // check if player is online, not OP (if executor is not OP), and in survival mode
        if (target == null || target.isOp && (s as Player).isOp.not() || target.gameMode != GameMode.SURVIVAL) {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // open inventory
        PlayerData.inInvSee[s as Player] = target
        s.openInventory(target.inventory)

        return false
    }
}
