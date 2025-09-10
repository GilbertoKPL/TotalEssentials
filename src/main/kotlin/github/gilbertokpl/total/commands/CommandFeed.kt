package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFeed : CommandManager("feed") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("comer"),
            active = MainConfig.feedActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.feed",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/feed",
                "totalessentials.commands.feed.other_/feed <playerName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val senderPlayer = sender as? Player

        // --------------------------------------------------------
        // Feed another player
        // --------------------------------------------------------
        if (args.size == 1) {
            // Check permission
            if (senderPlayer != null && !senderPlayer.hasPermission("totalessentials.commands.feed.other")) {
                senderPlayer.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            // Get target player
            val targetPlayer = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
                sender.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            // Check if target player is already full
            if (targetPlayer.foodLevel >= MAX_PLAYER_FOOD && MainConfig.feedNeedEatBelow) {
                sender.sendMessage(LangConfig.feedOtherFullMessage)
                return false
            }

            // Feed target player
            targetPlayer.foodLevel = MAX_PLAYER_FOOD
            targetPlayer.sendMessage(LangConfig.feedOtherMessage)
            sender.sendMessage(LangConfig.feedSuccessOtherMessage.replace("%player%", targetPlayer.name))
            return false
        }

        // --------------------------------------------------------
        // Feed self
        // --------------------------------------------------------
        if (senderPlayer == null) return true

        if (senderPlayer.foodLevel >= MAX_PLAYER_FOOD && MainConfig.feedNeedEatBelow) {
            senderPlayer.sendMessage(LangConfig.feedFullMessage)
            return false
        }

        senderPlayer.foodLevel = MAX_PLAYER_FOOD
        senderPlayer.sendMessage(LangConfig.feedMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_FOOD = 20
    }
}
