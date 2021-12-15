package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent

class PlayerBedEnterEvent : Listener {
    @EventHandler
    fun event(e: PlayerBedEnterEvent) {
        if (MainConfig.getInstance().antibugsBlockBed) {
            try {
                blockEnterInBed(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockEnterInBed(e: PlayerBedEnterEvent) {
        if (
            !e.player.hasPermission("essentialsk.resources.bypass.bed")) {
            e.isCancelled = true
        }
    }
}