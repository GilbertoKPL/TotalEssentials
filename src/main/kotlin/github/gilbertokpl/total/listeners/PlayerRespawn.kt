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

    companion object {
        private const val RESPAWN_DELAY_MS = 20L
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val task = TotalEssentials.getCore().getTask()

        task.async {
            delay(RESPAWN_DELAY_MS)

            task.sync {
                PlayerData.applyPlayerSettings(event.player)

                if (MainConfig.spawnSendToSpawnOnDeath) {
                    SpawnData.teleportToSpawn(event.player)
                }
            }
        }
    }
}
