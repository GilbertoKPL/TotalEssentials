package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockIgniteEvent

class IgniteEvent : Listener {

    companion object {
        private val BLOCKED_CAUSES = setOf(
            BlockIgniteEvent.IgniteCause.LAVA,
            BlockIgniteEvent.IgniteCause.SPREAD
        )
    }

    @EventHandler
    fun onBlockIgnite(event: BlockIgniteEvent) {
        if (!MainConfig.addonsBlockPropagationFire) return

        if (event.cause in BLOCKED_CAUSES) {
            event.isCancelled = true
        }
    }
}