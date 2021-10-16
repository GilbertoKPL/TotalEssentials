package me.gilberto.essentials.events

import me.gilberto.essentials.commands.kits.CreateKit
import me.gilberto.essentials.commands.kits.EditKit
import me.gilberto.essentials.commands.kits.Kit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseInventoryEvent : Listener {
    @EventHandler
    fun closeinventoy(e: InventoryCloseEvent) {
        if (!CreateKit().event(e)) return
        if (!EditKit().event(e)) return
    }
}