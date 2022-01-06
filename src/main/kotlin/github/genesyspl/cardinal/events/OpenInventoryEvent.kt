package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class OpenInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryOpenEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().containersBlockOpenEnable) {
            try {
                blockOpenInventory(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    //block open
    private fun blockOpenInventory(e: InventoryOpenEvent) {
        if (!e.player.hasPermission("cardinal.bypass.opencontainer") &&
            github.genesyspl.cardinal.configs.MainConfig.getInstance().containersBlockOpen.contains(e.inventory.type.name.lowercase())
        ) {
            e.isCancelled = true
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPermAction)
        }
    }
}