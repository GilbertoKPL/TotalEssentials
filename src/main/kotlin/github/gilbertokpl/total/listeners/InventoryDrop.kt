package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.LoginData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class InventoryDrop : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (!LoginData.isPlayerLoggedIn(event.player)) {
            event.isCancelled = true
        }
    }
}