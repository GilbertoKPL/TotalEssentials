package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.LoginData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPickupItemEvent

class PlayerPickup : Listener {

    @Suppress("DEPRECATION")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        if (!LoginData.isPlayerLoggedIn(event.player)) {
            event.isCancelled = true
        }
    }
}