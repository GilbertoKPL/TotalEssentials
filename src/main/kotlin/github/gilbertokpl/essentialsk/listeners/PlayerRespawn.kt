package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.SpawnData
import github.gilbertokpl.essentialsk.data.util.PlayerDataDAOUtil
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
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
        EssentialsK.instance.server.scheduler.runTaskLater(EssentialsK.instance, Runnable {
            PlayerDataDAOUtil.death(e.player)

            if (MainConfig.spawnSendToSpawnOnDeath) {
                try {
                    spawnRespawn(e)
                } catch (e: Throwable) {
                    FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
                }
            }

        }, 5L)
    }

    private fun spawnRespawn(e: PlayerRespawnEvent) {
        val p = e.player
        val loc = SpawnData["spawn"] ?: run {
            if (p.hasPermission("*")) {
                p.sendMessage(GeneralLang.spawnSendNotSet)
            }
            return
        }
        p.teleport(loc)
    }
}
