package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseInventoryEvent : Listener {
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
        Dao.getInstance().editKit[e.player].also {
            if (it == null) return false
            Dao.getInstance().editKit.remove(e.player)
            e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
            if (KitData(it).setItems(e.inventory.contents.filterNotNull().toList())) {
                e.player.sendMessage(GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", it))
            }
            return true
        }
    }
}