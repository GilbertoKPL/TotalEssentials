package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.`object`.SpawnData
import github.genesyspl.cardinal.data.start.PlayerDataLoader
import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawnEvent : Listener {
    @EventHandler
    fun event(e: PlayerRespawnEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().spawnSendToSpawnOnDeath) {
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
        github.genesyspl.cardinal.Cardinal.instance.server.scheduler.runTaskLater(
            github.genesyspl.cardinal.Cardinal.instance,
            Runnable {
                PlayerDataLoader.getInstance().death(e.player)
            },
            5L
        )
    }

    private fun spawnRespawn(e: PlayerRespawnEvent) {
        val loc = SpawnData("spawn")
        if (loc.checkCache()) {
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.respawnLocation = loc.getLocation()
    }
}