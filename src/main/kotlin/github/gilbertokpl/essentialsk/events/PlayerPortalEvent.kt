package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent

class PlayerPortalEvent : Listener {
    @EventHandler
    fun event(e: PlayerPortalEvent) {
        if (MainConfig.getInstance().antibugsBlockPlayerTeleportPortal) {
            try {
                blockPlayerTeleport(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPlayerTeleport(e: PlayerPortalEvent) {
        if (!e.player.hasPermission("essentialsk.bypass.teleportportal")) {
            e.player.sendMessage(GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
        }
    }
}