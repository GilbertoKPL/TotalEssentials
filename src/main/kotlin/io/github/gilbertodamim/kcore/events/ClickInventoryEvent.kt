package io.github.gilbertodamim.kcore.events


import io.github.gilbertodamim.kcore.commands.kits.EditKit
import io.github.gilbertodamim.kcore.commands.kits.Kit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ClickInventoryEvent : Listener {
    @EventHandler
    fun clickInventory(e: InventoryClickEvent) {
        if (Kit().kitGuiEvent(e)) return
        if (EditKit().editKitGuiEvent(e)) return
    }
}