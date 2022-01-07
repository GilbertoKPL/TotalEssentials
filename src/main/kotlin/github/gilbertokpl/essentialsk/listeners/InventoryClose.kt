package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryClose : Listener {
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        if (MainConfig.getInstance().kitsActivated) {
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
            p.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
            DataManager.getInstance().kitCacheV2[it]?.setItems(e.inventory.contents.filterNotNull().toList(), p)
                ?: return true
            return true
        }
    }
}