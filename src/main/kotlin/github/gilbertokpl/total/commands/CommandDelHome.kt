package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDelHome : CommandManager("delhome") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("deletarhome"),
            active = MainConfig.homesActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.delhome",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf(
                "/delhome <homeName>",
                "totalessentials.commands.delhome.other_/delhome <playername>:<homeName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val arg = args[0]

        // --------------------------------------------------------
        // Admin removing another player's home
        // --------------------------------------------------------
        if (arg.contains(":") && sender.hasPermission("totalessentials.commands.delhome.other")) {
            val (pNameRaw, homeNameRaw) = arg.split(":").let { it[0].lowercase() to it.getOrNull(1)?.lowercase() }

            val playerHomes = PlayerData.homeCache[pNameRaw] ?: run {
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            // If no specific home is provided, list homes
            if (homeNameRaw == null) {
                sender.sendMessage(
                    LangConfig.homesOtherList.replace("%player%", pNameRaw)
                        .replace("%list%", playerHomes.toString())
                )
                return false
            }

            // Check if home exists
            if (!playerHomes.contains(homeNameRaw)) {
                sender.sendMessage(LangConfig.homesNameDontExist)
                return false
            }

            // Remove home
            PlayerData.homeCache.remove(pNameRaw, homeNameRaw)
            sender.sendMessage(
                LangConfig.homesOtherRemoved.replace("%player%", pNameRaw)
                    .replace("%home%", homeNameRaw)
            )
            return false
        }

        // --------------------------------------------------------
        // Player removing own home
        // --------------------------------------------------------
        val player = sender as Player
        val homeName = arg.lowercase()
        val playerHomes = PlayerData.homeCache[player] ?: emptyMap()

        if (!playerHomes.contains(homeName)) {
            player.sendMessage(LangConfig.homesNameDontExist)
            return false
        }

        PlayerData.homeCache.remove(player, homeName)
        player.sendMessage(LangConfig.homesRemoved.replace("%home%", homeName))
        return false
    }
}
