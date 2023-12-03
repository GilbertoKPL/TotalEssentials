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
                "/cor remover",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args[0].lowercase() == "list") {
            s.sendMessage(
                LangConfig.colorSendList.replace(
                    "%colors%",
                    TotalEssentialsJava.basePlugin.getColor().list(s as Player).toString()
                )
            )
            return false
        }

        if (args[0].lowercase() == "remover") {
            PlayerData.colorCache[s as Player] = ""
            s.sendMessage(LangConfig.colorRemove)
            return false
        }

        if (args[0].lowercase() == "set" && args.size == 2) {

            val color = args[1].toCharArray()

            if (color.size == 2 && s.hasPermission("totalessentials.color.${args[1]}") && color[0].toString()
                    .contains("&") || color.size == 2 && s.hasPermission("totalessentials.color.*") && color[0].toString()
                    .contains("&")
            ) {
                PlayerData.colorCache[s as Player] = args[1].replace("&", "§")
                s.sendMessage(LangConfig.colorSet.replace("%color%", args[1].replace("&", "§") + "cor"))
                return false
            }
            s.sendMessage(LangConfig.colorNotSet)
            return false
        }

        return true
    }
}