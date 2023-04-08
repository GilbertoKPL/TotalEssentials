package github.gilbertokpl.total.listeners

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatEventLegend : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: ChatMessageEvent) {
        if (MainConfig.addonsColorInChat) {
            try {

                e.message = PlayerData.colorCache[e.sender] + PermissionUtil.colorPermission(e.sender, e.message)

            } catch (e: Throwable) {

            }
        }
    }
}