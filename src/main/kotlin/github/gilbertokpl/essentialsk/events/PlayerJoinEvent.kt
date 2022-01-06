package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.`object`.SpawnData
import github.gilbertokpl.essentialsk.data.start.PlayerDataLoader
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerJoinEvent) {
        e.joinMessage = null
        if (MainConfig.getInstance().spawnSendToSpawnOnLogin) {
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
        if (MainConfig.getInstance().vanishActivated) {
            try {
                vanishLoginEvent(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun vanishLoginEvent(e: PlayerJoinEvent) {
        if (e.player.hasPermission("essentialsk.commands.vanish") || e.player.hasPermission("essentialsk.bypass.vanish")) return
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
            e.player.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.player.teleport(SpawnData("spawn").getLocation())
    }
}