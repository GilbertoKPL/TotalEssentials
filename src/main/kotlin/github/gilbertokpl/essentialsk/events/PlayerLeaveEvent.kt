package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeaveEvent : Listener {
    @EventHandler
    fun event(e: PlayerQuitEvent) {
        try {
            PlayerData(e.player.name).unloadCache()
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }
}