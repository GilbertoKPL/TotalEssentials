package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent

class PlayerBedEnter : Listener {
    @EventHandler
    fun event(e: PlayerBedEnterEvent) {
        if (MainConfig.antibugsBlockBed) {
            try {
                blockEnterInBed(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockEnterInBed(e: PlayerBedEnterEvent) {
        if (!e.player.hasPermission("essentialsk.bypass.bed")) {
            e.player.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
