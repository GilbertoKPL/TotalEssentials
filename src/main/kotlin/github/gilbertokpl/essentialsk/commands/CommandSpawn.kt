package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.data.dao.SpawnData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.spawnActivated,
            consoleCanUse = true,
            commandName = "spawn",
            timeCoolDown = null,
            permission = "essentialsk.commands.spawn",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "P_/spawn",
                "essentialsk.commands.spawn.other_/spawn <playerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        val spawnCache = SpawnData["spawn"] ?: run {
            if ((s !is Player) || s.hasPermission("*")) {
                s.sendMessage(LangConfig.spawnSendNotSet)
            }
            return false
        }

        if (args.size == 1 || s !is Player) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.spawn.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            p.teleport(spawnCache)
            p.sendMessage(LangConfig.spawnSendOtherMessage)
            s.sendMessage(LangConfig.spawnSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        val playerCache = PlayerData[s] ?: return false

        if (playerCache.inTeleport) {
            s.sendMessage(LangConfig.spawnSendInTeleport)
            return false
        }

        val time = MainConfig.spawnTimeToTeleport

        if (s.hasPermission("essentialsk.bypass.teleport") || time == 0) {
            s.teleport(spawnCache)
            s.sendMessage(LangConfig.spawnSendMessage)
            return false
        }

        s.sendMessage(LangConfig.spawnSendTimeToTeleport.replace("%time%", time.toString()))

        val exe = TaskUtil.teleportExecutor(time)

        playerCache.inTeleport = true

        exe {
            playerCache.inTeleport = false
            s.teleport(spawnCache)
            s.sendMessage(LangConfig.spawnSendMessage)
        }

        return false
    }
}
