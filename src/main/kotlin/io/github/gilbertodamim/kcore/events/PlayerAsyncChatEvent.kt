package io.github.gilbertodamim.kcore.events

import io.github.gilbertodamim.kcore.commands.kits.EditKit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerAsyncChatEvent : Listener {
    @EventHandler
    fun closeInventory(e: AsyncPlayerChatEvent) {
        if (EditKit.editKitMessageEvent(e)) return
    }
}