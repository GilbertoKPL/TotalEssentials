package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandInvSee : CommandManager("invsee") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.invseeActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.invsee",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/invsee <playerName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // prevent seeing own inventory
        if (args[0].equals(sender.name.lowercase(), ignoreCase = true)) {
            sender.sendMessage(LangConfig.invseeSameName)
            return false
        }

        // get target player
        val target = TotalEssentials.getInstance().server.getPlayer(args[0])

        // check if player is online, not OP (if executor is not OP), and in survival mode
        if (target == null || target.isOp && (sender as Player).isOp.not() || target.gameMode != GameMode.SURVIVAL) {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // open inventory
        PlayerData.inInvSee[sender as Player] = target
        sender.openInventory(target.inventory)

        return false
    }
}
