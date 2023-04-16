package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MaterialUtil

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

class EntityChangeBlock : Listener {
    @EventHandler
    fun event(e: EntityChangeBlockEvent) {
        if (MainConfig.addonsBlockPlayerBreakPlantationFall) {
            try {
                blockPlayerBreakPlantation(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun blockPlayerBreakPlantation(e: EntityChangeBlockEvent) {
        if (e.block.type == MaterialUtil["soil"]) {
            e.isCancelled = true
        }
    }
}
