package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageEntity : Listener {
    @EventHandler
    fun event(e: EntityDamageByEntityEvent) {
        if (MainConfig.addonsBlockExplodeItems) {
            try {
                blockItemsExplode(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockItemsExplode(e: EntityDamageByEntityEvent) {
        if (e.entity is Item) {
            e.isCancelled = true
        }
    }
}
