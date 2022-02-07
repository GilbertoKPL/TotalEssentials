package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPing : Listener {
    @EventHandler
    fun event(e: ServerListPingEvent) {
        if (MainConfig.motdEnabled) {
            try {
                motd(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun motd(e: ServerListPingEvent) {
        val motd = MainUtil.getRandom(MainConfig.motdListMotd).replace(
            "%players_online%",
            PlayerUtil.getIntOnlinePlayers(false).toString()
        ).replace("\\n", "\n")
        e.motd = motd
    }
}
