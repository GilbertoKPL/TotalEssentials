package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTphere : CommandManager("tphere") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("puxar"),
            active = MainConfig.tphereActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tphere",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/tphere <playerName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        // check if player is online
        val p = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        p.teleportSafe((sender as Player).location)

        p.sendMessage(
            LangConfig.tphereTeleportedOtherSuccess
        )
        sender.sendMessage(
            LangConfig.tphereTeleportedSuccess.replace("%player%", p.name)
        )
        return false
    }
}
