package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.economy.MoneyManager
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMoney : CommandManager("money") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("dinheiro", "coin"),
            active = MainConfig.moneyActivated,
            target = CommandTargetType.ALL,
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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // show player own money
        if (args.isEmpty() && sender is Player) {
            val money = PlayerData.moneyCache[sender] ?: 0.0
            sender.sendMessage(MoneyManager.replaceMoney(LangConfig.moneyMessage, money))
            return false
        }

        if (args.isEmpty()) return true

        // show top money players
        if (args[0] == "top") {
            MoneyManager.refreshTycoon()
            sender.sendMessage(LangConfig.moneyTopMessage)
            var position = 1
            for (i in MoneyManager.tycoonPlayer) {
                sender.sendMessage(
                    MoneyManager.replaceMoney(LangConfig.moneyTop, i.value)
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
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }
            val otherMoney = PlayerData.moneyCache[args[0]] ?: 0.0
            sender.sendMessage(
                MoneyManager.replaceMoney(LangConfig.moneyMessageOther, otherMoney)
                    .replace("%player%", args[0].lowercase())
            )
            return false
        }

        // pay another player
        if (args[0] == "pay" && args.size == 3 && sender is Player) {
            val value = args[2].toDoubleOrNull() ?: return true
            if (value <= 0) return true

            if (args[1].equals(sender.name, ignoreCase = true)) {
                sender.sendMessage(LangConfig.moneyPaySame)
                return false
            }

            if (!PlayerData.checkIfPlayerExists(args[1])) {
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            val money = PlayerData.moneyCache[sender] ?: 0.0
            if (money < value) {
                sender.sendMessage(MoneyManager.replaceMoney(LangConfig.moneyMissing, value - money))
                return false
            }

            // transfer money
            MoneyManager.withdrawPlayer(sender.name, value)
            MoneyManager.depositPlayer(args[1], value)

            sender.sendMessage(
                MoneyManager.replaceMoney(LangConfig.moneyPay, value)
                    .replace("%player%", args[1].lowercase())
            )

            PlayerUtil.sendMessage(
                args[1].lowercase(),
                MoneyManager.replaceMoney(LangConfig.moneyPayOther, value)
                    .replace("%player%", sender.name)
            )

            return false
        }

        // admin commands: set, give, take
        if ((sender !is Player || sender.hasPermission("totalessentials.commands.money.admin")) && args.size == 3) {
            val value = args[2].toDoubleOrNull() ?: return true
            if (value < 0) return true

            if (!PlayerData.checkIfPlayerExists(args[1])) {
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            when (args[0]) {
                "set" -> {
                    PlayerData.moneyCache[args[1]] = value
                    MoneyManager.refreshTycoon()
                    sender.sendMessage(
                        MoneyManager.replaceMoney(LangConfig.moneySet, value)
                        .replace("%player%", args[1].lowercase()))
                    PlayerUtil.sendMessage(args[1].lowercase(), MoneyManager.replaceMoney(LangConfig.moneySetOther, value))
                }
                "take" -> {
                    val otherMoney = PlayerData.moneyCache[args[1]] ?: return true
                    if (otherMoney < value) {
                        sender.sendMessage(MoneyManager.replaceMoney(LangConfig.moneyMissing, value - otherMoney))
                        return false
                    }
                    PlayerData.moneyCache[args[1]] = otherMoney - value
                    MoneyManager.refreshTycoon()
                    sender.sendMessage(
                        MoneyManager.replaceMoney(LangConfig.moneyTake, value)
                        .replace("%player%", args[1].lowercase()))
                    PlayerUtil.sendMessage(
                        args[1].lowercase(),
                        MoneyManager.replaceMoney(LangConfig.moneyTakeOther, value)
                    )
                }
                "give" -> {
                    val otherMoney = PlayerData.moneyCache[args[1]] ?: 0.0
                    PlayerData.moneyCache[args[1]] = otherMoney + value
                    MoneyManager.refreshTycoon()
                    sender.sendMessage(
                        MoneyManager.replaceMoney(LangConfig.moneyAdd, value)
                        .replace("%player%", args[1].lowercase()))
                    PlayerUtil.sendMessage(args[1].lowercase(), MoneyManager.replaceMoney(LangConfig.moneyAddOther, value))
                }
            }
            return false
        }

        return true
    }
}
