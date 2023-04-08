package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveEvent : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerMoveEvent) {
        if (!MainConfig.antiafkEnabled) return
        if (!e.player.hasPermission("totalessentials.bypass.antiafk")) {
            PlayerData.afk[e.player] = 1
        }
        if (!LoginData.isPlayerLoggedIn(e.player)) {
            e.isCancelled = true
        }
    }
}