package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPing : Listener {
    @EventHandler
    fun event(e: ServerListPingEvent) {
        if (MainConfig.getInstance().motdEnabled) {
            try {
                motd(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun motd(e: ServerListPingEvent) {
        val motd = MainConfig.getInstance().motdListMotd.random().replace(
            "%players_online%",
            PlayerUtil.getInstance().getIntOnlinePlayers(MainConfig.getInstance().onlineCountRemoveVanish).toString()
        ).replace("\\n", "\n")
        e.motd = motd
    }
}