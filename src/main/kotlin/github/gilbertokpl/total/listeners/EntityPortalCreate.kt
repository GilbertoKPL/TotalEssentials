package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class EntityPortalCreate : Listener {
    @EventHandler
    fun event(e: PortalCreateEvent) {
        if (MainConfig.antibugsBlockCreatePortal) {
            try {
                blockCreationPortal(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockCreationPortal(e: PortalCreateEvent) {
        e.isCancelled = true
        try {
            if (e.entity is Player) {
                (e.entity as Player).sendMessage(LangConfig.generalNotPermAction)
            }
        } catch (ignored: NoSuchMethodError) {
        }
    }
}
