package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData

import github.gilbertokpl.total.util.MainUtil

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerQuitEvent) {
        e.quitMessage = null
        if (MainConfig.backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Throwable) {

            }
        }
        try {
            if (!PlayerData.vanishCache[e.player]!! && !e.player.hasPermission("*")) {
                if (MainConfig.messagesLeaveMessage) {
                    MainUtil.serverMessage(
                        LangConfig.messagesLeaveMessage
                            .replace("%player%", e.player.name)
                    )
                }
                if (MainConfig.discordbotSendLeaveMessage) {
                    sendLeaveEmbed(e)
                }
            }
        } catch (e: Throwable) {

        }
    }

    private fun setBackLocation(e: PlayerQuitEvent) {
        if (!e.player.hasPermission("essentialsk.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
        PlayerData.backLocation[e.player] = e.player.location
    }

    private fun sendLeaveEmbed(e: PlayerQuitEvent) {

    }
}
