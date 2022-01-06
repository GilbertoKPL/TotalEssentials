package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PermissionUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class SignChangeEvent : Listener {
    @EventHandler
    fun event(e: SignChangeEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsColorInSign) {
            try {
                signColor(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun signColor(e: SignChangeEvent) {
        for (i in 0 until e.lines.size) {
            e.setLine(i, PermissionUtil.getInstance().colorPermission(e.player, e.getLine(i) ?: continue))
        }
    }
}