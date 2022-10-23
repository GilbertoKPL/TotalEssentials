package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData
import github.gilbertokpl.total.cache.SpawnData
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : github.gilbertokpl.base.external.command.CommandCreator("spawn") {

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

        if (args.isEmpty() && s !is Player) {
            return true
        }

        val spawnCache = SpawnData.spawnLocation["spawn"] ?: run {
            if ((s !is Player) || s.hasPermission("*")) {
                s.sendMessage(LangConfig.spawnNotSet)
            }
            return false
        }

        if (args.size == 1 || s !is Player) {

            //check perms
            if (s is Player && !s.hasPermission("totalessentials.commands.spawn.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            p.teleport(spawnCache)
            p.sendMessage(LangConfig.spawnOtherMessage)
            s.sendMessage(
                LangConfig.spawnSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                )
            )

            return false
        }

        val inTeleport = PlayerData.inTeleport[s]
        if (inTeleport != null && inTeleport) {
            s.sendMessage(LangConfig.homesInTeleport)
            return false
        }

        val time = MainConfig.spawnTimeToTeleport

        if (s.hasPermission("totalessentials.bypass.teleport") || time == 0) {
            s.teleport(spawnCache)
            s.sendMessage(LangConfig.spawnMessage)
            return false
        }

        s.sendMessage(
            LangConfig.spawnTimeToTeleport.replace(
                "%time%",
                time.toString()
            )
        )

        val exe = TaskUtil.teleportExecutor(time)

        PlayerData.inTeleport[s] = true

        exe {
            PlayerData.inTeleport[s] = false
            s.teleport(spawnCache)
            s.sendMessage(LangConfig.spawnMessage)
        }

        return false
    }
}
