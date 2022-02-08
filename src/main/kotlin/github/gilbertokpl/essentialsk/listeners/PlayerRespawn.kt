package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.SpawnData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.loader.PlayerSet.values
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.okkero.skedule.SynchronizationContext
import github.okkero.skedule.schedule
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawn : Listener {
    @EventHandler
    fun event(e: PlayerRespawnEvent) {
        try {
            playerData(e)
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun playerData(e: PlayerRespawnEvent) {
        EssentialsK.instance.server.scheduler.schedule(EssentialsK.instance, SynchronizationContext.SYNC) {
            waitFor(20)
            e.player.values(PlayerData[e.player] ?: return@schedule)

            if (MainConfig.spawnSendToSpawnOnDeath) {
                try {
                    spawnRespawn(e)
                } catch (e: Throwable) {
                    FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
                }
            }

        }
    }

    private fun spawnRespawn(e: PlayerRespawnEvent) {
        val p = e.player
        val loc = SpawnData["spawn"] ?: run {
            if (p.hasPermission("*")) {
                p.sendMessage(LangConfig.spawnNotSet)
            }
            return
        }
        p.teleport(loc)
    }
}
