package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent

class BurnEvent : Listener {
    @EventHandler
    fun event(e: BlockBurnEvent) {
        if (MainConfig.addonsBlockPropagationFire) {
            try {
                blockPropagationFire(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPropagationFire(e: BlockBurnEvent) {
        e.isCancelled = true
    }
}
