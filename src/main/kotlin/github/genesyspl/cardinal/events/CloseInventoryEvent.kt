package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().kitsActivated) {
            try {
                if (editKitInventoryCloseEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitInventoryCloseEvent(e: InventoryCloseEvent): Boolean {
        val p = e.player as Player
        DataManager.getInstance().editKit[p].also {
            if (it == null) return false
            DataManager.getInstance().editKit.remove(p)
            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
            DataManager.getInstance().kitCacheV2[it]?.setItems(e.inventory.contents.filterNotNull().toList(), p)
                ?: return true
            return true
        }
    }
}