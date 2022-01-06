package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.PlayerUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGamemode : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "gamemode"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.gamemode"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage = listOf(
        "P_/gamemode <number>",
        "cardinal.commands.gamemode.other_/gamemode <number> <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val playerGameMode = PlayerUtil.getInstance().getGamemodeNumber(args[0])

        if (args.size == 1 && s is Player) {

            //check if player is in same gamemode
            if (s.gameMode == playerGameMode) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().gamemodeSameGamemode)
                return false
            }

            (DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false).setGamemode(
                playerGameMode,
                args[0].toInt()
            )
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().gamemodeUseSuccess.replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            return false
        }

        if (args.size == 2) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.gamemode.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[1]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            //check if player is in same gamemode
            if (p.gameMode == playerGameMode) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().gamemodeSameOtherGamemode)
                return false
            }

            (DataManager.getInstance().playerCacheV2[p.name.lowercase()] ?: return false).setGamemode(
                playerGameMode,
                args[0].toInt()
            )
            p.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().gamemodeUseOtherSuccess.replace(
                    "%gamemode%",
                    playerGameMode.name.lowercase()
                )
            )
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().gamemodeSendSuccessOtherMessage.replace(
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
}