package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.SpawnData
import github.genesyspl.cardinal.data.start.PlayerDataLoader
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.ReflectUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerJoinEvent) {
        e.joinMessage = null
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().spawnSendToSpawnOnLogin) {
            try {
                spawnLogin(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            PlayerDataLoader.getInstance().loadCache(e.player)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().vanishActivated) {
            try {
                vanishLoginEvent(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun vanishLoginEvent(e: PlayerJoinEvent) {
        if (e.player.hasPermission("cardinal.commands.vanish") || e.player.hasPermission("cardinal.bypass.vanish")) return
        for (it in ReflectUtil.getInstance().getPlayers()) {
            if (DataManager.getInstance().playerCacheV2[it.name.lowercase()]?.vanishCache ?: continue) {
                @Suppress("DEPRECATION")
                e.player.hidePlayer(it)
            }
        }
    }

    private fun spawnLogin(e: PlayerJoinEvent) {
        val loc = SpawnData("spawn")
        if (loc.checkCache()) {
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.player.teleport(SpawnData("spawn").getLocation())
    }
}