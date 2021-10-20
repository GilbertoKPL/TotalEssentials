package io.github.gilbertodamim.essentials.events

import io.github.gilbertodamim.essentials.commands.kits.CreateKit
import io.github.gilbertodamim.essentials.commands.kits.EditKit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseInventoryEvent : Listener {
    @EventHandler
    fun closeInventory(e: InventoryCloseEvent) {
        if (CreateKit().event(e)) return
        if (EditKit().event(e)) return
    }
}