package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

class InventoryClose : Listener {
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        if (MainConfig.kitsActivated) {
            try {
                if (editKitInventoryCloseEvent(e)) return
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.invseeActivated) {
            try {
                invseeInventoryCloseEvent(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitInventoryCloseEvent(e: InventoryCloseEvent): Boolean {
        val p = e.player as Player
        DataManager.editKit[p].also {
            if (it == null) return false
            DataManager.editKit.remove(p)
            val dataInstance = KitData[it] ?: return true

            dataInstance.setItems(e.inventory.contents.filterNotNull().toList())
            e.player.sendMessage(GeneralLang.kitsEditKitSuccess.replace("%kit%", dataInstance.kitNameCache))

            return true
        }
    }

    private fun invseeInventoryCloseEvent(e: InventoryCloseEvent) {
        val p = e.player as Player
        val playerInstance = PlayerData[p] ?: return

        if (playerInstance.inInvsee != null && e.inventory.type == InventoryType.PLAYER) {
            playerInstance.inInvsee = null
        }
    }
}
