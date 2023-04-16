package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGamemode : github.gilbertokpl.core.external.command.CommandCreator("gamemode") {

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
        val playerGameMode = PlayerUtil.getGameModeNumber(args[0])

        if (args.size == 1 && s is Player) {

            //check if player is in same gamemode
            if (s.gameMode == playerGameMode) {
                s.sendMessage(LangConfig.gamemodeSameGamemode)
                return false
            }

            setGamemode(playerGameMode, s)

            s.sendMessage(
                LangConfig.gamemodeUseSuccess.replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            return false
        }

        if (args.size == 2) {

            //check perms
            if (s is Player && !s.hasPermission("totalessentials.commands.gamemode.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[1]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            //check if player is in same gamemode
            if (p.gameMode == playerGameMode) {
                s.sendMessage(LangConfig.gamemodeSameOtherGamemode)
                return false
            }

            setGamemode(playerGameMode, p)

            p.sendMessage(
                LangConfig.gamemodeUseOtherSuccess.replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            s.sendMessage(
                LangConfig.gamemodeSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                ).replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            return false
        }

        return true
    }

    private fun setGamemode(gm: GameMode, player: Player) {

        val gamemodeNumber = PlayerUtil.getNumberGameMode(gm)

        PlayerData.gameModeCache[player] = gamemodeNumber

        player.gameMode = gm

        //desbug fly on set gamemode 0
        if (gm == GameMode.SURVIVAL && PlayerData.flyCache[player]!!) {
            player.allowFlight = true
            player.isFlying = true
        }

    }
}
