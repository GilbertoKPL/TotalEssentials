package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteract : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!LoginData.isPlayerLoggedIn(event.player)) {
            event.isCancelled = true
            return
        }

        if (MainConfig.addonsInfinityAnvil) {
            repairAnvil(event)
        }
    }

    private fun repairAnvil(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val clickedBlock = event.clickedBlock ?: return

        if (clickedBlock.type == Material.ANVIL) {
            clickedBlock.type = Material.ANVIL
        }
    }
}