package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

class EntityChangeBlockEvent : Listener {
    @EventHandler
    fun event(e: EntityChangeBlockEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsBlockPlayerBreakPlantationFall) {
            try {
                blockPlayerBreakPlantation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPlayerBreakPlantation(e: EntityChangeBlockEvent) {
        if (e.block.type == DataManager.getInstance().material["soil"]) {
            e.isCancelled = true
        }
    }
}