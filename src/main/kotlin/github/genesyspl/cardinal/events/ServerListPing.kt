package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPing : Listener {
    @EventHandler
    fun event(e: ServerListPingEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().motdEnabled) {
            try {
                motd(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun motd(e: ServerListPingEvent) {
        val motd = github.genesyspl.cardinal.configs.MainConfig.getInstance().motdListMotd.random().replace(
            "%players_online%",
            PlayerUtil.getInstance().getIntOnlinePlayers(false).toString()
        ).replace("\\n", "\n")
        e.motd = motd
    }
}