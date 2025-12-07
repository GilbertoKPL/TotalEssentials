package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class InventoryOpen : Listener {

    private val blockedInventories by lazy {
        MainConfig.containersBlockOpen.map { it.lowercase() }.toSet()
    }

    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!MainConfig.containersBlockOpenEnable) return
        if (event.player.hasPermission("totalessentials.bypass.opencontainer")) return

        val inventoryType = event.inventory.type.name.lowercase()

        if (blockedInventories.contains(inventoryType)) {
            event.isCancelled = true
            event.player.sendMessage(LangConfig.generalNotPermAction)
        }
    }
}
