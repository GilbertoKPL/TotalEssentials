package io.github.gilbertodamim.kcore.events

import io.github.gilbertodamim.kcore.commands.kits.Kit.Companion.eventLeaveRemoveCache
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeaveEvent : Listener {
    @EventHandler
    fun closeInventory(e: PlayerQuitEvent) {
        if (eventLeaveRemoveCache(e)) return
    }
}