package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMove : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val to = event.to ?: return

        if (event.from.distanceSquared(to) == 0.0) return

        if (!LoginData.isPlayerLoggedIn(player)) {
            event.isCancelled = true
            return
        }

        if (MainConfig.antiafkEnabled && !player.hasPermission("totalessentials.bypass.antiafk")) {
            PlayerData.afk[player] = 1
        }
    }
}