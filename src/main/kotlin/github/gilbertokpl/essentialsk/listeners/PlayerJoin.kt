package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.data.dao.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerJoinEvent) {
        e.joinMessage = null

        try {
            PlayerData.loadCache(e)
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
    }
}
