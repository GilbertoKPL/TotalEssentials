package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent

class BurnEvent : Listener {
    @EventHandler
    fun event(e: BlockBurnEvent) {
        if (MainConfig.addonsBlockPropagationFire) {
            blockPropagationFire(e)
        }
    }

    private fun blockPropagationFire(e: BlockBurnEvent) {
        e.isCancelled = true
    }
}
