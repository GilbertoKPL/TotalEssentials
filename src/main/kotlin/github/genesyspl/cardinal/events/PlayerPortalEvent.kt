package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent

class PlayerPortalEvent : Listener {
    @EventHandler
    fun event(e: PlayerPortalEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().antibugsBlockPlayerTeleportPortal) {
            try {
                blockPlayerTeleport(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPlayerTeleport(e: PlayerPortalEvent) {
        if (!e.player.hasPermission("cardinal.bypass.teleportportal")) {
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
        }
    }
}