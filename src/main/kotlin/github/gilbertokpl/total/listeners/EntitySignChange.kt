package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PermissionUtil

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class EntitySignChange : Listener {
    @EventHandler
    fun event(e: SignChangeEvent) {
        if (MainConfig.addonsColorInSign) {
            try {
                signColor(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun signColor(e: SignChangeEvent) {
        for (i in 0 until e.lines.size) {
            e.setLine(i, PermissionUtil.colorPermission(e.player, e.getLine(i) ?: continue))
        }
    }
}
