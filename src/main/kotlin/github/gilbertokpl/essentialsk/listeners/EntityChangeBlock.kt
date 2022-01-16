package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.MaterialUtil
import org.apache.commons.lang3.exception.ExceptionUtils
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
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPlayerBreakPlantation(e: EntityChangeBlockEvent) {
        if (e.block.type == MaterialUtil["soil"]) {
            e.isCancelled = true
        }
    }
}
