package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
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
            maximumSize = 1,
            commandUsage = listOf(
                "P_/money",
                "P_/money pay <playerName> <value>",
                "/money top",
                "essentialsk.commands.money.admin/money set <playerName> <value>",
                "essentialsk.commands.money.admin/money take <playerName> <value>",
                "essentialsk.commands.money.admin/money add <playerName> <value>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s is Player) {
            val playerData = PlayerData[s] ?: return true

            return false
        }

        if (args[0] == "pay" && args.size == 3 && s is Player) {
            return false
        }

        if (args[0] == "top") {
            return false
        }

        if ((s !is Player || s.hasPermission("essentialsk.commands.money.admin")) && args.size == 3) {
            if (args[0] == "set") {

                return false
            }
            if (args[0] == "take") {

                return false
            }
            if (args[0] == "add") {

                return false
            }
            return true
        }

        return true
    }

}