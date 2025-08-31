package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.MainConfig
import kotlinx.coroutines.delay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawn : Listener {
    @EventHandler
    fun event(e: PlayerRespawnEvent) {
        try {
            playerData(e)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun playerData(e: PlayerRespawnEvent) {
        val task = TotalEssentials.getCore().getTask()
        task.async {
            delay(20)
            task.sync {
                PlayerData.applyPlayerSettings(e.player)

                if (MainConfig.spawnSendToSpawnOnDeath) {
                    SpawnData.teleportToSpawn(e.player)
                }
            }
        }
    }
}
