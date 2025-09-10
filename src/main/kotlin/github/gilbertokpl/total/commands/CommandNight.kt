package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNight : CommandManager("night") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("noite"),
            active = MainConfig.nightActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.night",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/night",
                "/noite"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        Bukkit.getWorlds().forEach { it.time = 14000 }
        sender.sendMessage(LangConfig.nightConsoleSet)
        return false
    }
}