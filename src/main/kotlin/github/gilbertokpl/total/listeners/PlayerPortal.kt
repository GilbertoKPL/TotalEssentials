package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent

class PlayerPortal : Listener {

    @EventHandler
    fun onPlayerPortal(event: PlayerPortalEvent) {
        if (!MainConfig.antibugsBlockPlayerTeleportPortal) return
        if (event.player.hasPermission("totalessentials.bypass.teleportportal")) return

        event.player.sendMessage(LangConfig.generalNotPermAction)
        event.isCancelled = true
    }
}