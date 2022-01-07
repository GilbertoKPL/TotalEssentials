package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class PlayerInteractEntity : Listener {
    @EventHandler
    fun event(e: PlayerInteractEntityEvent) {
        if (MainConfig.getInstance().antibugsBlockNametag) {
            try {
                blockNameTag(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockNameTag(e: PlayerInteractEntityEvent) {
        @Suppress("DEPRECATION")
        if (e.player.itemInHand.type == Material.NAME_TAG &&
            !e.player.hasPermission("essentialsk.bypass.nametag")
        ) {
            e.player.sendMessage(GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
        }
    }
}
