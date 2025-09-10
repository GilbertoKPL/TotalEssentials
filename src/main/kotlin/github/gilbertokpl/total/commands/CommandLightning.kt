package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLightning : CommandManager("lightning") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("raio", "thor"),
            active = MainConfig.lightningActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.lightning",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/lightning <Player>",
                "P_/lightning"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender !is Player) {
                return true
            }

            val targetBlock = sender.getTargetBlockExact(50) ?: return false

            val loc = targetBlock.location.add(0.5, 1.0, 0.5)
            sender.world.strikeLightning(loc)
            sender.sendMessage(LangConfig.lightningMessage)
            return true
        }

        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return true
        }

        val world = target.world
        world.strikeLightning(target.location)

        sender.sendMessage(LangConfig.lightningOtherMessage.replace("%player%", target.name))
        return true
    }
}
