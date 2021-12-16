package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

class EntityChangeBlockEvent : Listener {
    @EventHandler
    fun event(e: EntityChangeBlockEvent) {
        if (MainConfig.getInstance().addonsBlockPlayerBreakPlantationFall) {
            try {
                blockPlayerBreakPlatantion(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPlayerBreakPlatantion(e: EntityChangeBlockEvent) {
        if (e.block.type == Dao.getInstance().material["soil"]) {
            e.isCancelled = true
        }
    }
}