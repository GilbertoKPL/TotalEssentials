package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockIgniteEvent

class BlockIgniteEvent : Listener {
    @EventHandler
    fun event(e: BlockIgniteEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsBlockPropagationFire) {
            try {
                blockPropagationFire(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPropagationFire(e: BlockIgniteEvent) {
        if (e.cause == BlockIgniteEvent.IgniteCause.LAVA || e.cause == BlockIgniteEvent.IgniteCause.SPREAD) {
            e.isCancelled = true
        }
    }
}