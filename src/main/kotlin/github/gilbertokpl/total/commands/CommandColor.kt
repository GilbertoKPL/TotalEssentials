package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandColor : github.gilbertokpl.core.external.command.CommandCreator("color") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("cor"),
            active = MainConfig.colorActivated,
            target = CommandTarget.PLAYER,
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

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val player = s as? Player ?: return false

        when (args[0].lowercase()) {

            "list" -> {
                val colors = TotalEssentialsJava.getBasePlugin().getColor().list(player).toString()
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
