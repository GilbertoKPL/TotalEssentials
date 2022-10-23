package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent

class PlayerPortal : Listener {
    @EventHandler
    fun event(e: PlayerPortalEvent) {
        if (MainConfig.antibugsBlockPlayerTeleportPortal) {
            try {
                blockPlayerTeleport(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockPlayerTeleport(e: PlayerPortalEvent) {
        if (!e.player.hasPermission("essentialsk.bypass.teleportportal")) {
            e.player.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
