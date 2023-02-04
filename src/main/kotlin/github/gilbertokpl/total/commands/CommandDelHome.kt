package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDelHome : github.gilbertokpl.core.external.command.CommandCreator("delhome") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("deletarhome"),
            active = MainConfig.homesActivated,
            target = CommandTarget.PLAYER,
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

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        //admin
        if (args[0].contains(":") && s.hasPermission("totalessentials.commands.delhome.other")) {
            val split = args[0].split(":")

            val pName = split[0].lowercase()

            val playerData = PlayerData.homeCache[pName] ?: run {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (split.size < 2) {
                s.sendMessage(
                    LangConfig.homesOtherList.replace("%player%", pName)
                        .replace("%list%", PlayerData.homeCache[pName].toString())
                )
                return false
            }

            if (!playerData.contains(split[1])) {
                s.sendMessage(LangConfig.homesNameDontExist)
                return false
            }

            PlayerData.homeCache.remove(pName, split[1])

            s.sendMessage(
                LangConfig.homesOtherRemoved.replace("%player%", pName)
                    .replace("%home%", split[1])
            )
            return false
        }

        val p = s as Player

        val nameHome = args[0].lowercase()

        //check if home don't exist
        if (!(PlayerData.homeCache[p] ?: return false).contains(nameHome)) {
            p.sendMessage(LangConfig.homesNameDontExist)
            return false
        }

        PlayerData.homeCache.remove(p, nameHome)

        p.sendMessage(LangConfig.homesRemoved.replace("%home%", nameHome))
        return false
    }
}
