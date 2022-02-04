package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.modify.GameModeCache.setGamemode
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGamemode : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.gamemodeActivated,
            consoleCanUse = true,
            commandName = "gamemode",
            timeCoolDown = null,
            permission = "essentialsk.commands.gamemode",
            minimumSize = 1,
            maximumSize = 2,
            commandUsage = listOf(
                "P_/gamemode <number>",
                "essentialsk.commands.gamemode.other_/gamemode <number> <PlayerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val playerGameMode = PlayerUtil.getGamemodeNumber(args[0])

        if (args.size == 1 && s is Player) {

            //check if player is in same gamemode
            if (s.gameMode == playerGameMode) {
                s.sendMessage(GeneralLang.gamemodeSameGamemode)
                return false
            }

            (PlayerData[s] ?: return false).setGamemode(
                playerGameMode, s
            )
            s.sendMessage(
                GeneralLang.gamemodeUseSuccess.replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            return false
        }

        if (args.size == 2) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.gamemode.other")) {
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[1]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            //check if player is in same gamemode
            if (p.gameMode == playerGameMode) {
                s.sendMessage(GeneralLang.gamemodeSameOtherGamemode)
                return false
            }

            (PlayerData[p] ?: return false).setGamemode(
                playerGameMode, p
            )

            p.sendMessage(
                GeneralLang.gamemodeUseOtherSuccess.replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            s.sendMessage(
                GeneralLang.gamemodeSendSuccessOtherMessage.replace("%player%", p.name).replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            return false
        }

        return true
    }
}
