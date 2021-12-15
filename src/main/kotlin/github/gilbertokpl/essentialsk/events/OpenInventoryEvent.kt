package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class OpenInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryOpenEvent) {
        if (MainConfig.getInstance().containersBlockOpenEnable) {
            try {
                blockOpenInventory(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    //block open
    private fun blockOpenInventory(e: InventoryOpenEvent) {
        if (!e.player.hasPermission("essentialsk.bypass.opencontainer") &&
            MainConfig.getInstance().containersBlockOpen.contains(e.inventory.type.name.lowercase())
        ) {
            e.isCancelled = true
            e.player.sendMessage(GeneralLang.getInstance().generalNotPermAction)
        }
    }
}