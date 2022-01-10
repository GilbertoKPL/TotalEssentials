package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class EntityPortalCreate : Listener {
    @EventHandler
    fun event(e: PortalCreateEvent) {
        if (MainConfig.antibugsBlockCreatePortal) {
            try {
                blockCreationPortal(e)
            } catch (e: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockCreationPortal(e: PortalCreateEvent) {
        e.isCancelled = true
        try {
            if (e.entity is Player) {
                (e.entity as Player).sendMessage(GeneralLang.generalNotPermAction)
            }
        } catch (ignored: NoSuchMethodError) {
        }
    }
}
