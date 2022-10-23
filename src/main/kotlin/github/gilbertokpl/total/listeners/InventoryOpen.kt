package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class InventoryOpen : Listener {
    @EventHandler
    fun event(e: InventoryOpenEvent) {
        if (MainConfig.containersBlockOpenEnable) {
            try {
                blockOpenInventory(e)
            } catch (e: Throwable) {

            }
        }
    }

    //block open
    private fun blockOpenInventory(e: InventoryOpenEvent) {
        if (!e.player.hasPermission("essentialsk.bypass.opencontainer") &&
            MainConfig.containersBlockOpen.contains(e.inventory.type.name.lowercase())
        ) {
            e.isCancelled = true
            e.player.sendMessage(LangConfig.generalNotPermAction)
        }
    }
}
