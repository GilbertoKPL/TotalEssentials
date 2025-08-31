package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpeed : CommandCreator("speed") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.speedActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.speed",
            minimumSize = 1,
            maximumSize = 2,
            usage = listOf(
                "/speed <value>",
                "/speed remove",
                "totalessentials.commands.speed.other_/speed <player> <value>",
                "totalessentials.commands.speed.other_/speed <player> remove"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // Single argument: set own speed or remove
        if (args.size == 1 && s is Player) {
            if (args[0].equals("remove", true) || args[0].equals("remover", true)) {
                clearSpeed(s)
                s.sendMessage(LangConfig.speedRemove)
                return false
            }

            val speed = args[0].toIntOrNull() ?: return true

            if (speed !in 0..10) {
                s.sendMessage(LangConfig.speedIncorrectValue)
                return false
            }

            setSpeed(speed, s)
            s.sendMessage(LangConfig.speedSuccess.replace("%value%", speed.toString()))
            return false
        }

        // Two arguments: set/remove speed for another player
        if (args.size != 2) return true

        if (s is Player && !s.hasPermission("totalessentials.commands.speed.other")) {
            s.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        val target = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        if (args[1].equals("remove", true) || args[1].equals("remover", true)) {
            clearSpeed(target)
            s.sendMessage(LangConfig.speedRemoveOther.replace("%player%", target.name))
            target.sendMessage(LangConfig.speedOtherRemove)
            return false
        }

        val speed = args[1].toIntOrNull() ?: return true

        if (speed !in 0..10) {
            s.sendMessage(LangConfig.speedIncorrectValue)
            return false
        }

        setSpeed(speed, target)
        s.sendMessage(LangConfig.speedSuccessOther.replace("%player%", target.name).replace("%value%", speed.toString()))
        target.sendMessage(LangConfig.speedOtherSuccess.replace("%value%", speed.toString()))
        return false
    }

    private fun setSpeed(speed: Int, player: Player) {
        PlayerData.speedCache[player] = speed
        player.walkSpeed = (speed * 0.1f)
        player.flySpeed = (speed * 0.1f)
    }

    private fun clearSpeed(player: Player) {
        PlayerData.speedCache[player] = 1
        player.walkSpeed = 0.2f
        player.flySpeed = 0.1f
    }
}
