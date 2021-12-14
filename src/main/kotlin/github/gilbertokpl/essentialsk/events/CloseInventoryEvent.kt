package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.commands.CommandEditKit
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        try {
            if (CommandEditKit.getInstance().editKitInventoryCloseEvent(e)) return
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }
}