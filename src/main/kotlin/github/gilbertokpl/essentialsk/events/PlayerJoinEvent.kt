package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent : Listener {
    @EventHandler
    fun playerJoinEvent(e: PlayerJoinEvent) {
        try {
            PlayerData(e.player.name).loadCache()
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }
}