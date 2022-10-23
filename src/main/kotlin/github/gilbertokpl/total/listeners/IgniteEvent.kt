package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockIgniteEvent

class IgniteEvent : Listener {
    @EventHandler
    fun event(e: BlockIgniteEvent) {
        if (MainConfig.addonsBlockPropagationFire) {
            try {
                blockPropagationFire(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockPropagationFire(e: BlockIgniteEvent) {
        if (e.cause == BlockIgniteEvent.IgniteCause.LAVA || e.cause == BlockIgniteEvent.IgniteCause.SPREAD) {
            e.isCancelled = true
        }
    }
}
