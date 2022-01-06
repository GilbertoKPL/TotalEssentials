package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageByEntityEvent : Listener {
    @EventHandler
    fun event(e: EntityDamageByEntityEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsBlockExplodeItems) {
            try {
                blockItemsExplode(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockItemsExplode(e: EntityDamageByEntityEvent) {
        if (e.entity is Item) {
            e.isCancelled = true
        }
    }
}