package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.LoginData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPickupItemEvent

class PlayerPickup : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: PlayerPickupItemEvent) {
        if (!LoginData.checkIfPlayerIsLoggedIn(e.player)) {
            e.isCancelled = true
            return
        }
    }
}