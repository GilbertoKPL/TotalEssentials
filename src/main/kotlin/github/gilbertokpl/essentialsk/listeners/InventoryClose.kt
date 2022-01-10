package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryClose : Listener {
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        if (MainConfig.kitsActivated) {
            try {
                if (editKitInventoryCloseEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitInventoryCloseEvent(e: InventoryCloseEvent): Boolean {
        val p = e.player as Player
        DataManager.editKit[p].also {
            if (it == null) return false
            DataManager.editKit.remove(p)
            p.sendMessage(GeneralLang.generalSendingInfoToDb)
            KitDataV2[it]?.setItems(e.inventory.contents.filterNotNull().toList(), p)
                ?: return true
            return true
        }
    }
}
