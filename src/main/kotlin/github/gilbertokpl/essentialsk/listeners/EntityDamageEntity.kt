package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageEntity : Listener {
    @EventHandler
    fun event(e: EntityDamageByEntityEvent) {
        if (MainConfig.addonsBlockExplodeItems) {
            try {
                blockItemsExplode(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockItemsExplode(e: EntityDamageByEntityEvent) {
        if (e.entity is Item) {
            e.isCancelled = true
        }
    }
}
