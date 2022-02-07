package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class PlayerInteractEntity : Listener {
    @EventHandler
    fun event(e: PlayerInteractEntityEvent) {
        if (MainConfig.antibugsBlockNametag) {
            try {
                blockNameTag(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockNameTag(e: PlayerInteractEntityEvent) {
        @Suppress("DEPRECATION")
        if (e.player.itemInHand.type == Material.NAME_TAG &&
            !e.player.hasPermission("essentialsk.bypass.nametag")
        ) {
            e.player.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
