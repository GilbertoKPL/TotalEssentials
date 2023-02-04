package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataManager
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPlayTime : github.gilbertokpl.core.external.command.CommandCreator("playtime") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("time"),
            active = MainConfig.playtimeActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.playtime",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/playtime",
                "/playtime <player>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s is Player) {
            DataManager.playTimeGuiCache[1].also {
                it ?: run {
                    s.sendMessage(LangConfig.shopNotExistShop)
                    return false
                }
                s.openInventory(it)
            }
            return false
        }

        if (!PlayerData.checkIfPlayerExist(args[0])) {
            s.sendMessage(LangConfig.generalPlayerNotExist)
            return false
        }

        val pTime = PlayerData.playtimeLocal[args[0]] ?: 0L

        val time =
            ((PlayerData.playTimeCache[args[0]]) ?: 0L) + if (pTime != 0L) (System.currentTimeMillis() - pTime) else 0L

        s.sendMessage(
            LangConfig.playtimeMessage.replace("%player%", args[0])
                .replace("%time%", TotalEssentials.basePlugin.getTime().convertMillisToString(time, false))
        )

        return false
    }
}