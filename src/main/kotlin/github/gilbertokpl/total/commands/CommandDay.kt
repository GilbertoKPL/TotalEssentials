package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDay : CommandManager("day") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("dia"),
            active = MainConfig.dayActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.day",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/day",
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        Bukkit.getWorlds().forEach { it.time = 1000 }
        sender.sendMessage(LangConfig.dayConsoleSet)
        return false
    }
}