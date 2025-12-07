package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

class EntityChangeBlock : Listener {

    @EventHandler
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (!MainConfig.addonsBlockPlayerBreakPlantationFall) return

        if (event.block.type == MaterialUtil["soil"]) {
            event.isCancelled = true
        }
    }
}