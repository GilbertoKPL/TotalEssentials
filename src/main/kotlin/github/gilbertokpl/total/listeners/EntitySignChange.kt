package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class EntitySignChange : Listener {

    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        if (!MainConfig.addonsColorInSign) return

        for (i in 0 until event.lines.size) {
            val line = event.getLine(i) ?: continue
            event.setLine(i, PermissionUtil.colorPermission(event.player, line))
        }
    }
}