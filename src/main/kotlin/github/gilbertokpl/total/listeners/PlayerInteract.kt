package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteract : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: PlayerInteractEvent) {

        if (!LoginData.isPlayerLoggedIn(e.player)) {
            e.isCancelled = true
            return
        }

        if (MainConfig.addonsInfinityAnvil) {
            try {
                infinityAnvil(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun infinityAnvil(e: PlayerInteractEvent) {
        e.clickedBlock ?: return
        if (e.action == Action.RIGHT_CLICK_BLOCK && e.clickedBlock!!.type == Material.ANVIL) {
            e.clickedBlock!!.type = Material.ANVIL
        }
    }
}
