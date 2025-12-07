package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class EntityPortalCreate : Listener {

    @EventHandler
    fun onPortalCreate(event: PortalCreateEvent) {
        if (MainConfig.antibugsBlockCreatePortal) {
            event.isCancelled = true
        }
    }
}