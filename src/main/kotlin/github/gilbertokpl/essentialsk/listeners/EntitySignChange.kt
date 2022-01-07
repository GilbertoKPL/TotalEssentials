package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class EntitySignChange : Listener {
    @EventHandler
    fun event(e: SignChangeEvent) {
        if (MainConfig.getInstance().addonsColorInSign) {
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