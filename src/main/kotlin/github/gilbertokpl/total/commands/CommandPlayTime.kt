package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.inventory.Playtime
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPlayTime : CommandManager("playtime") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("tempo"),
            active = MainConfig.playtimeActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.playtime",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/playtime",
                "/playtime <player>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // Determine target player name
        val targetName = if (args.isEmpty() && sender is Player) sender.name else args.getOrNull(0)
            ?: return false

        // Only player can run without args
        if (args.isEmpty() && sender !is Player) return false

        // Check if player exists in cache or offline
        if (!PlayerData.checkIfPlayerExists(targetName)) {
            sender.sendMessage(LangConfig.generalPlayerNotExist)
            return false
        }

        // Calculate total playtime
        val lastLogin = PlayerData.playtimeLocal[targetName] ?: 0L
        val cachedTime = PlayerData.playTimeCache[targetName] ?: 0L
        val totalTime = if (lastLogin != 0L) cachedTime + (System.currentTimeMillis() - lastLogin) else cachedTime

        // Open GUI for self player
        if (args.isEmpty() && sender is Player) {
            val inv = Data.playTimeInventoryCache[1]
            if (inv == null) {
                sender.sendMessage(LangConfig.shopNotExistShop)
                return false
            }
            sender.openInventory(inv)
            inv.setItem(31, Playtime.createHeadItem(sender.name, totalTime))
            return false
        }

        // Send playtime message to sender
        sender.sendMessage(
            LangConfig.playtimeMessage
                .replace("%player%", targetName)
                .replace(
                    "%time%",
                    TotalEssentials.getCore().getTime().convertMillisToString(totalTime, false)
                )
        )

        return false
    }
}
