package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.commands.CommandVanish
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommandEvent : Listener {
    @EventHandler
    fun playerPreCommandEvent(e: PlayerCommandPreprocessEvent) {
        try {
            val split = e.message.split(" ")
            CommandVanish.getInstance().vanishPreCommandEvent(e, split)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }
}