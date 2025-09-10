package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandColor : CommandManager("color") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("cor"),
            active = MainConfig.colorActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.color",
            minimumSize = 1,
            maximumSize = 2,
            usage = listOf(
                "/cor list",
                "/cor set <&cor>",
                "/cor remover"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val player = sender as? Player ?: return false

        when (args[0].lowercase()) {

            "list" -> {
                val colors = TotalEssentials.getCore().getColor().list(player).toString()
                player.sendMessage(LangConfig.colorSendList.replace("%colors%", colors))
                return false
            }

            "remover" -> {
                PlayerData.colorCache[player] = ""
                player.sendMessage(LangConfig.colorRemove)
                return false
            }

            "set" -> {
                if (args.size != 2) return true

                val colorCode = args[1]
                val hasPermission = player.hasPermission("totalessentials.color.${colorCode}") ||
                        player.hasPermission("totalessentials.color.*")

                if (colorCode.length == 2 && colorCode.startsWith("&") && hasPermission) {
                    val formattedColor = colorCode.replace("&", "§")
                    PlayerData.colorCache[player] = formattedColor
                    player.sendMessage(LangConfig.colorSet.replace("%color%", "$formattedColor cor"))
                } else {
                    player.sendMessage(LangConfig.colorNotSet)
                }

                return false
            }

            else -> return true
        }
    }
}
