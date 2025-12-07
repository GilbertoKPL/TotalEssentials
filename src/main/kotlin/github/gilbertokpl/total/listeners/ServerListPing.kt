package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import github.gilbertokpl.total.util.PlayerUtil

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPing : Listener {

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        if (!MainConfig.motdEnabled) return

        val motdList = if (TotalEssentials.getInstance().server.hasWhitelist()) {
            MainConfig.motdListMotdWhitelist
        } else {
            MainConfig.motdListMotd
        }

        event.motd = ServerUtil.getRandom(motdList).formatMotd()
    }

    private fun String.formatMotd(): String {
        return this
            .replace("%players_online%", PlayerUtil.getOnlinePlayersCount(false).toString())
            .replace("\\n", "\n")
            .replace("&", "§")
    }
}
