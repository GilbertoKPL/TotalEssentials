package github.gilbertokpl.total.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent


class PlayerKickEvent : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun kick(e: PlayerKickEvent) {
        e.leaveMessage = null
    }
}