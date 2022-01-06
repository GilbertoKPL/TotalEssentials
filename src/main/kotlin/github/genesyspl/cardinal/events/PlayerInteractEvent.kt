package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractEvent : Listener {
    @EventHandler
    fun event(e: PlayerInteractEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsInfinityAnvil) {
            try {
                infinityAnvil(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
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