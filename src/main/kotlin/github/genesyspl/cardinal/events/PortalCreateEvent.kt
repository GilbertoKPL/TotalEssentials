package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class PortalCreateEvent : Listener {
    @EventHandler
    fun event(e: PortalCreateEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().antibugsBlockCreatePortal) {
            try {
                blockCreationPortal(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockCreationPortal(e: PortalCreateEvent) {
        e.isCancelled = true
        try {
            if (e.entity is Player) {
                (e.entity as Player).sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPermAction)
            }
        } catch (ignored: NoSuchMethodError) {
        }
    }
}