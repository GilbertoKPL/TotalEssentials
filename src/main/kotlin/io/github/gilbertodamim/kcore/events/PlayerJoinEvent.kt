package io.github.gilbertodamim.kcore.events

import io.github.gilbertodamim.kcore.commands.kits.Kit.Companion.eventJoinPutCache
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent : Listener {
    @EventHandler
    fun closeInventory(e: PlayerJoinEvent) {
        if (eventJoinPutCache(e)) return
    }
}