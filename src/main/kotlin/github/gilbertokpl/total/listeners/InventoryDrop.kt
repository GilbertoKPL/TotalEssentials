package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.LoginData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class InventoryDrop : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: PlayerDropItemEvent) {
        if (!LoginData.isPlayerLoggedIn(e.player)) {
            e.isCancelled = true
            return
        }
    }
}