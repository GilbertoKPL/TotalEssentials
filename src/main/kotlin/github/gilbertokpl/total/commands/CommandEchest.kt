package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEchest : CommandManager("echest") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("ec"),
            active = MainConfig.echestActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.ec",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/echest",
                "totalessentials.commands.ec.other_/ec <PlayerName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val player = sender as Player

        // --------------------------------------------------------
        // Open own Ender Chest
        // --------------------------------------------------------
        if (args.isEmpty()) {
            player.sendMessage(LangConfig.echestSuccess)
            player.openInventory(player.enderChest)
            return false
        }

        // --------------------------------------------------------
        // Admin opening another player's Ender Chest
        // --------------------------------------------------------
        if (!player.hasPermission("totalessentials.commands.ec.other")) return true

        val targetPlayer = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
            player.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        player.sendMessage(LangConfig.echestOtherSuccess.replace("%player%", targetPlayer.name))
        player.openInventory(targetPlayer.enderChest)
        return false
    }
}
