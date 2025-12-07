package github.gilbertokpl.total.listeners

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatEventLegend : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onChatMessage(event: ChatMessageEvent) {
        if (!MainConfig.addonsColorInChat) return

        val playerColor = PlayerData.colorCache[event.sender] ?: ""
        val coloredMessage = PermissionUtil.colorPermission(event.sender, event.message)

        event.message = playerColor + coloredMessage
    }
}