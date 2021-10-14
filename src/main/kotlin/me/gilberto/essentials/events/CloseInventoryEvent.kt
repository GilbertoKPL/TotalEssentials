package me.gilberto.essentials.events

import me.gilberto.essentials.commands.Kits
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseInventoryEvent : Listener {
    @EventHandler
    fun closeinventoy(e: InventoryCloseEvent) {
        if (!Kits().event(e)) return
    }
}