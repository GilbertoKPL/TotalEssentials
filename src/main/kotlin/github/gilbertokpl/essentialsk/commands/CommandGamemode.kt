package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGamemode : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.gamemode"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage = listOf(
        "P_/gamemode <number>",
        "CP_/gamemode <PlayerName> <number>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val playerOrGameMode: Any? = when (args[0]) {
            "0" -> GameMode.SURVIVAL
            "1" -> GameMode.CREATIVE
            "2" -> try {
                GameMode.ADVENTURE
            } catch (e: Exception) {
                null
            }
            "3" -> try {
                GameMode.SPECTATOR
            } catch (e: Exception) {
                null
            }
            else -> EssentialsK.instance.server.getPlayer(args[0])
        }

        if (playerOrGameMode == null && args.size == 2) {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        if (playerOrGameMode == null && args.size == 1) {
            return true
        }

        if (playerOrGameMode is GameMode && args.size == 1 && s is Player) {
            PlayerData(s.name.lowercase()).setGamemode(playerOrGameMode, args[0].toInt())
            s.sendMessage(GeneralLang.getInstance().gamemodeUseSuccess.replace("%gamemode%", playerOrGameMode.name))
            return false
        }

        if (playerOrGameMode is Player && args.size == 2) {
            val playerGameMode: GameMode = when (args[1]) {
                "0" -> GameMode.SURVIVAL
                "1" -> GameMode.CREATIVE
                "2" -> try {
                    GameMode.ADVENTURE
                } catch (e: Exception) {
                    null
                }
                "3" -> try {
                    GameMode.SPECTATOR
                } catch (e: Exception) {
                    null
                }
                else -> null
            } ?: return true

            PlayerData(playerOrGameMode.name.lowercase()).setGamemode(playerGameMode, args[1].toInt())
            playerOrGameMode.sendMessage(
                GeneralLang.getInstance().gamemodeUseOtherSuccess.replace(
                    "%gamemode%",
                    playerOrGameMode.name
                )
            )


            return false
        }

        return true
    }
}