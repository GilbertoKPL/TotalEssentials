package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamage : Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (!MainConfig.addonsBlockPlayerGoToVoid) return
        if (event.cause != EntityDamageEvent.DamageCause.VOID) return

        val player = event.entity as? Player ?: return

        if (player.location.blockY < 0) {
            event.isCancelled = true
            player.fallDistance = 1.0f
            SpawnData.teleportToSpawn(player)
        }
    }
}