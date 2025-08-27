package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.FoliaUtil.teleportSafe
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : github.gilbertokpl.core.external.command.CommandCreator("spawn") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.spawnActivated,
            target = CommandTarget.ALL,
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

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) return true

        val spawnLocation = SpawnData.spawnLocation["spawn"] ?: run {
            if (s !is Player || s.hasPermission("*")) {
                s.sendMessage(LangConfig.spawnNotSet)
            }
            return false
        }

        // Teleport another player
        if (args.size == 1) {

            if (s is Player && !s.hasPermission("totalessentials.commands.spawn.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            val target = TotalEssentialsJava.getInstance().server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            target.teleportSafe(spawnLocation)
            target.sendMessage(LangConfig.spawnOtherMessage)
            s.sendMessage(
                LangConfig.spawnSuccessOtherMessage.replace("%player%", target.name)
            )
            return false
        }

        // Teleport self
        if (s is Player) {
            PlayerUtil.teleportWithTime(s, spawnLocation, MainConfig.spawnTimeToTeleport, LangConfig.spawnMessage, "spawn")
        }

        return false
    }
}
