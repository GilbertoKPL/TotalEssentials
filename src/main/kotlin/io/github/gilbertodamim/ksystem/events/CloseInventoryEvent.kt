package io.github.gilbertodamim.ksystem.events

import io.github.gilbertodamim.ksystem.commands.kits.CreateKit
import io.github.gilbertodamim.ksystem.commands.kits.EditKit
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