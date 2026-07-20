package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.chat.ChatManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ChatSessionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) = ChatManager.joinPlayer(event.player)

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) = ChatManager.leavePlayer(event.player)
}
