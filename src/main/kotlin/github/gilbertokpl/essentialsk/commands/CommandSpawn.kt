package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.data.dao.SpawnDataDAO
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : CommandCreator {
    override val active: Boolean = MainConfig.spawnActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "spawn"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.spawn"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage =
        listOf(
            "P_/spawn",
            "essentialsk.commands.spawn.other_/spawn <playerName>"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        val spawnCache = SpawnDataDAO["spawn"] ?: run {
            if ((s !is Player) || s.hasPermission("*")) {
                s.sendMessage(GeneralLang.spawnSendNotSet)
            }
            return false
        }

        if (args.size == 1 || s !is Player) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.spawn.other")) {
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            p.teleport(spawnCache)
            p.sendMessage(GeneralLang.spawnSendOtherMessage)
            s.sendMessage(GeneralLang.spawnSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        val playerCache = PlayerDataDAO[s] ?: return false

        if (playerCache.inTeleport) {
            s.sendMessage(GeneralLang.spawnSendInTeleport)
            return false
        }

        if (s.hasPermission("essentialsk.bypass.teleport")) {
            s.teleport(spawnCache)
            s.sendMessage(GeneralLang.spawnSendMessage)
            return false
        }

        val time = MainConfig.spawnTimeToTeleport

        s.sendMessage(GeneralLang.spawnSendTimeToTeleport.replace("%time%", time.toString()))

        val exe = TaskUtil.teleportExecutor(time)

        playerCache.inTeleport = true

        exe {
            playerCache.inTeleport = false
            s.teleport(spawnCache)
            s.sendMessage(GeneralLang.spawnSendMessage)
        }

        return false
    }
}
