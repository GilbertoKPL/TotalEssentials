package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.util.MoneyUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMoney : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.moneyActivated,
            consoleCanUse = true,
            commandName = "money",
            timeCoolDown = null,
            permission = "essentialsk.commands.money",
            minimumSize = 0,
            maximumSize = 3,
            commandUsage = listOf(
                "P_/money",
                "/money <PlayerName>",
                "P_/money pay <playerName> <value>",
                "/money top",
                "essentialsk.commands.money.admin_/money set <playerName> <value>",
                "essentialsk.commands.money.admin_/money take <playerName> <value>",
                "essentialsk.commands.money.admin_/money add <playerName> <value>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s is Player) {

            val money = PlayerData[s]?.moneyCache ?: return true

            s.sendMessage(MoneyUtil.coinReplacer(
                LangConfig.moneyMessage, money
            ))
            return false
        }

        if (args[0] == "top") {
            return false
        }

        if (args.size == 1) {
            val moneyOther = PlayerData[args[0].lowercase()]?.moneyCache ?: run {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            s.sendMessage(MoneyUtil.coinReplacer(
                LangConfig.moneyMessageOther, moneyOther
            ).replace("%player%", args[0].lowercase()))
            return false
        }

        if (args[0] == "pay" && args.size == 3 && s is Player) {

            val value = args[2].toDoubleOrNull() ?: return true

            if (value <= 0) return true

            val playerData = PlayerData[args[1].lowercase()] ?: run {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            val money = PlayerData[s] ?: return true

            if (money.moneyCache < value) {
                s.sendMessage(MoneyUtil.coinReplacer(
                    LangConfig.moneyMissing, value - money.moneyCache
                ))
                return false
            }

            money.moneyCache -= value
            playerData.moneyCache += value

            s.sendMessage(MoneyUtil.coinReplacer(
                LangConfig.moneyPay, value
            ).replace("%player%", args[1].lowercase()))

            PlayerUtil.sendMessage(args[1].lowercase(), MoneyUtil.coinReplacer(
                LangConfig.moneyPayOther, value
            ).replace("%player%", s.name))

            return false
        }

        if ((s !is Player || s.hasPermission("essentialsk.commands.money.admin")) && args.size == 3) {


            val value = args[2].toDoubleOrNull() ?: return true

            if (value <= 0) return true

            val playerData = PlayerData[args[1].lowercase()] ?: run {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (args[0] == "set") {
                playerData.moneyCache = value

                s.sendMessage(MoneyUtil.coinReplacer(
                    LangConfig.moneySet, value
                ).replace("%player%", args[1].lowercase()))

                PlayerUtil.sendMessage(args[1].lowercase(), MoneyUtil.coinReplacer(
                    LangConfig.moneySetOther, value
                ))

                return false
            }
            if (args[0] == "take") {
                if (playerData.moneyCache < value) {
                    s.sendMessage(MoneyUtil.coinReplacer(
                        LangConfig.moneyMissing, value - playerData.moneyCache
                    ))
                    return false
                }
                playerData.moneyCache -= value

                s.sendMessage(MoneyUtil.coinReplacer(
                    LangConfig.moneyTake, value
                ).replace("%player%", args[1].lowercase()))
                PlayerUtil.sendMessage(args[1].lowercase(), MoneyUtil.coinReplacer(
                    LangConfig.moneyTakeOther, value
                ))

                return false
            }
            if (args[0] == "add") {
                playerData.moneyCache += value

                s.sendMessage(MoneyUtil.coinReplacer(
                    LangConfig.moneyAdd, value
                ).replace("%player%", args[1].lowercase()))

                PlayerUtil.sendMessage(args[1].lowercase(), MoneyUtil.coinReplacer(
                    LangConfig.moneyAddOther, value
                ))

                return false
            }
        }
        return true
    }

}