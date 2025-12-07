package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : CommandManager("spawn") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(),
            active = MainConfig.spawnActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.spawn",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/spawn",
                "totalessentials.commands.spawn.other_/spawn <playerName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && sender !is Player) return true

        val spawnLocation = SpawnData.spawnLocation["spawn"] ?: run {
            if (sender !is Player || sender.hasPermission("*")) {
                sender.sendMessage(LangConfig.spawnNotSet)
            }
            return false
        }

        // Teleport another player
        if (args.size == 1) {

            if (sender is Player && !sender.hasPermission("totalessentials.commands.spawn.other")) {
                sender.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            val target = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
                sender.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            target.teleportSafe(spawnLocation)
            target.sendMessage(LangConfig.spawnOtherMessage)
            sender.sendMessage(
                LangConfig.spawnSuccessOtherMessage.replace("%player%", target.name)
            )
            return false
        }

        // Teleport self
        if (sender is Player) {
            PlayerUtil.teleportWithDelay(sender, spawnLocation, MainConfig.spawnTimeToTeleport, LangConfig.spawnMessage, "spawn")
        }

        return false
    }
}
