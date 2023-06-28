package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataManager
import github.gilbertokpl.total.cache.internal.PlaytimeInventory
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPlayTime : github.gilbertokpl.core.external.command.CommandCreator("playtime") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("tempo"),
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

        val playerName = if (args.isEmpty() && s is Player) s.name else args[0]

        val playerTimeMillis = PlayerData.playtimeLocal[playerName] ?: 0L

        val playerTime = ((PlayerData.playTimeCache[playerName]) ?: 0L) + if (playerTimeMillis != 0L) (System.currentTimeMillis() - playerTimeMillis) else 0L

        if (args.isEmpty() && s is Player) {
            DataManager.playTimeInventoryCache[1].also {
                it ?: run {
                    s.sendMessage(LangConfig.shopNotExistShop)
                    return false
                }
                s.openInventory(it)
            }?.setItem(31, PlaytimeInventory.createHeadItem(s.name, playerTime))
            return false
        }

        if (args[0].lowercase() == "fix" && s !is Player) {

            for (i in PlayerData.playTimeCache.getMap()) {
                if (i.value!! > 31557600000) {
                    PlayerData.playTimeCache[i.key] = 0
                }
            }

            return true
        }

        if (!PlayerData.checkIfPlayerExist(args[0])) {
            s.sendMessage(LangConfig.generalPlayerNotExist)
            return false
        }

        s.sendMessage(
            LangConfig.playtimeMessage.replace("%player%", args[0])
                .replace("%time%", TotalEssentials.basePlugin.getTime().convertMillisToString(playerTime, false))
        )

        return false
    }
}