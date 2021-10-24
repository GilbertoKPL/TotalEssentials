package io.github.gilbertodamim.ksystem.events


import io.github.gilbertodamim.ksystem.commands.kits.EditKit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ClickInventoryEvent : Listener {
    @EventHandler
    fun clickInventory(e: InventoryClickEvent) {
        if (EditKit().editKitGuiEvent(e)) return
    }
}