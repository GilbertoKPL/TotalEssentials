package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGamemode : CommandCreator("gamemode") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("gm"),
            active = MainConfig.gamemodeActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.gamemode",
            minimumSize = 1,
            maximumSize = 2,
            usage = listOf(
                "P_/gamemode <number>",
                "totalessentials.commands.gamemode.other_/gamemode <number> <PlayerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val senderPlayer = s as? Player
        val targetGameMode = PlayerUtil.getGameModeNumber(args[0])

        // --------------------------------------------------------
        // Set gamemode self
        // --------------------------------------------------------
        if (args.size == 1 && senderPlayer != null) {
            if (senderPlayer.gameMode == targetGameMode) {
                senderPlayer.sendMessage(LangConfig.gamemodeSameGamemode)
                return false
            }

            applyGamemode(senderPlayer, targetGameMode)
            senderPlayer.sendMessage(
                LangConfig.gamemodeUseSuccess.replace("%gamemode%", targetGameMode.name.lowercase())
            )
            return false
        }

        // --------------------------------------------------------
        // Set gamemode another player
        // --------------------------------------------------------
        if (args.size == 2) {
            if (senderPlayer != null && !senderPlayer.hasPermission("totalessentials.commands.gamemode.other")) {
                senderPlayer.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            val target = TotalEssentials.getInstance().server.getPlayer(args[1]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (target.gameMode == targetGameMode) {
                s.sendMessage(LangConfig.gamemodeSameOtherGamemode)
                return false
            }

            applyGamemode(target, targetGameMode)

            target.sendMessage(
                LangConfig.gamemodeUseOtherSuccess.replace("%gamemode%", targetGameMode.name.lowercase())
            )

            s.sendMessage(
                LangConfig.gamemodeSuccessOtherMessage
                    .replace("%player%", target.name)
                    .replace("%gamemode%", targetGameMode.name.lowercase())
            )

            return false
        }

        return true
    }

    private fun applyGamemode(player: Player, gm: GameMode) {
        val gmNumber = PlayerUtil.getNumberGameMode(gm)
        PlayerData.gameModeCache[player] = gmNumber

        player.gameMode = gm

        // Corrige bug de fly no gamemode survival
        if (gm == GameMode.SURVIVAL && PlayerData.flyCache[player] == true) {
            player.allowFlight = true
            player.isFlying = true
        }
    }
}
