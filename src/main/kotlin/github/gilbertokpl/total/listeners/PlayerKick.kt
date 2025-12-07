package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent

class PlayerKick : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerKick(event: PlayerKickEvent) {
        if (TotalEssentials.isLowVersion()) {
            event.leaveMessage = ""
        }
    }
}