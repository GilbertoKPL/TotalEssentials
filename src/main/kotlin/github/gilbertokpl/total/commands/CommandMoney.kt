package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.economy.CoreMoney
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMoney : CommandCreator("money") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("dinheiro"),
            active = MainConfig.moneyActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.money",
            minimumSize = 0,
            maximumSize = 3,
            usage = listOf(
                "P_/money",
                "/money <PlayerName>",
                "P_/money pay <playerName> <value>",
                "/money top",
                "totalessentials.commands.money.admin_/money set <player> <value>",
                "totalessentials.commands.money.admin_/money take <player> <value>",
                "totalessentials.commands.money.admin_/money give <player> <value>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // show player own money
        if (args.isEmpty() && s is Player) {
            val money = PlayerData.moneyCache[s] ?: 0.0
            s.sendMessage(CoreMoney.coinReplacer(LangConfig.moneyMessage, money))
            return false
        }

        if (args.isEmpty()) return true

        // show top money players
        if (args[0] == "top") {
            s.sendMessage(LangConfig.moneyTopMessage)
            var position = 1
            for (i in CoreMoney.tycoonPlayer) {
                s.sendMessage(
                    CoreMoney.coinReplacer(LangConfig.moneyTop, i.value)
                        .replace("%player%", i.key)
                        .replace("%position%", position.toString())
                )
                position++
            }
            return false
        }

        // show another player's money
        if (args.size == 1) {
            if (!PlayerData.checkIfPlayerExists(args[0])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }
            val otherMoney = PlayerData.moneyCache[args[0]] ?: 0.0
            s.sendMessage(
                CoreMoney.coinReplacer(LangConfig.moneyMessageOther, otherMoney)
                    .replace("%player%", args[0].lowercase())
            )
            return false
        }

        // pay another player
        if (args[0] == "pay" && args.size == 3 && s is Player) {
            val value = args[2].toDoubleOrNull() ?: return true
            if (value <= 0) return true

            if (args[1].equals(s.name, ignoreCase = true)) {
                s.sendMessage(LangConfig.moneyPaySame)
                return false
            }

            if (!PlayerData.checkIfPlayerExists(args[1])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            val money = PlayerData.moneyCache[s] ?: 0.0
            if (money < value) {
                s.sendMessage(CoreMoney.coinReplacer(LangConfig.moneyMissing, value - money))
                return false
            }

            // transfer money
            CoreMoney.withdrawPlayer(s.name, value)
            CoreMoney.depositPlayer(args[1], value)

            s.sendMessage(
                CoreMoney.coinReplacer(LangConfig.moneyPay, value)
                    .replace("%player%", args[1].lowercase())
            )

            PlayerUtil.sendMessage(
                args[1].lowercase(),
                CoreMoney.coinReplacer(LangConfig.moneyPayOther, value)
                    .replace("%player%", s.name)
            )

            return false
        }

        // admin commands: set, give, take
        if ((s !is Player || s.hasPermission("totalessentials.commands.money.admin")) && args.size == 3) {
            val value = args[2].toDoubleOrNull() ?: return true
            if (value < 0) return true

            if (!PlayerData.checkIfPlayerExists(args[1])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            when (args[0]) {
                "set" -> {
                    PlayerData.moneyCache[args[1]] = value
                    s.sendMessage(
                        CoreMoney.coinReplacer(LangConfig.moneySet, value)
                        .replace("%player%", args[1].lowercase()))
                    PlayerUtil.sendMessage(args[1].lowercase(), CoreMoney.coinReplacer(LangConfig.moneySetOther, value))
                }
                "take" -> {
                    val otherMoney = PlayerData.moneyCache[args[1]] ?: return true
                    if (otherMoney < value) {
                        s.sendMessage(CoreMoney.coinReplacer(LangConfig.moneyMissing, value - otherMoney))
                        return false
                    }
                    PlayerData.moneyCache[args[1]] = otherMoney - value
                    s.sendMessage(
                        CoreMoney.coinReplacer(LangConfig.moneyTake, value)
                        .replace("%player%", args[1].lowercase()))
                    PlayerUtil.sendMessage(
                        args[1].lowercase(),
                        CoreMoney.coinReplacer(LangConfig.moneyTakeOther, value)
                    )
                }
                "give" -> {
                    val otherMoney = PlayerData.moneyCache[args[1]] ?: 0.0
                    PlayerData.moneyCache[args[1]] = otherMoney + value
                    s.sendMessage(
                        CoreMoney.coinReplacer(LangConfig.moneyAdd, value)
                        .replace("%player%", args[1].lowercase()))
                    PlayerUtil.sendMessage(args[1].lowercase(), CoreMoney.coinReplacer(LangConfig.moneyAddOther, value))
                }
            }
            return false
        }

        return true
    }
}
