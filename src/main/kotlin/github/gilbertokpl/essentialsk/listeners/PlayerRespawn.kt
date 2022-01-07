package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.`object`.SpawnData
import github.gilbertokpl.essentialsk.data.start.PlayerDataLoader
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawn : Listener {
    @EventHandler
    fun event(e: PlayerRespawnEvent) {
        if (MainConfig.getInstance().spawnSendToSpawnOnDeath) {
            try {
                spawnRespawn(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            playerData(e)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun playerData(e: PlayerRespawnEvent) {
        EssentialsK.instance.server.scheduler.runTaskLater(EssentialsK.instance, Runnable {
            PlayerDataLoader.getInstance().death(e.player)
        }, 5L)
    }

    private fun spawnRespawn(e: PlayerRespawnEvent) {
        val loc = SpawnData("spawn")
        if (loc.checkCache()) {
            e.player.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.respawnLocation = loc.getLocation()
    }
}