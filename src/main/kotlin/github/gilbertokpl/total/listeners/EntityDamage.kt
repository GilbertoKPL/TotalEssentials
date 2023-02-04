package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamage : Listener {
    @EventHandler
    fun event(e: EntityDamageEvent) {
        if (MainConfig.addonsBlockPlayerGoToVoid) {
            try {
                blockPlayerFallInVoid(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockPlayerFallInVoid(e: EntityDamageEvent) {
        if (e.entity is Player && e.cause == EntityDamageEvent.DamageCause.VOID) {
            val p = e.entity as Player
            if (p.location.blockY < 0) {
                e.isCancelled = true
                p.fallDistance = 1.0f
                SpawnData.teleport(p)
            }
        }
    }
}
