package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.commands.CommandEditKit
import github.gilbertokpl.essentialsk.commands.CommandKit
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ClickInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryClickEvent) {
        try {
            if (CommandEditKit.getInstance().editKitInventoryClickEvent(e)) return
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
        try {
            if (CommandKit.getInstance().kitGuiEvent(e)) return
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }
}