package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerQuitEvent) {
        e.quitMessage = null

        LoginData.isLoggedIn[e.player] = false

        // Handle back location
        if (MainConfig.backActivated) {
            try {
                setBackLocation(e)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }

        // Handle leave messages
        try {
            val isVanished = PlayerData.vanishCache[e.player] ?: false
            if (!isVanished && !e.player.hasPermission("*")) {
                if (MainConfig.messagesLeaveMessage) {
                    ServerUtil.serverMessage(
                        LangConfig.messagesLeaveMessage.replace("%player%", e.player.name)
                    )
                }
                if (MainConfig.discordbotSendLeaveMessage) sendLeaveEmbed(e)
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        // Handle playtime
        try {
            if (MainConfig.playtimeActivated) {
                val lastLoginTime = PlayerData.playtimeLocal[e.player] ?: System.currentTimeMillis()
                val previousTime = PlayerData.playTimeCache[e.player] ?: 0L

                var totalTime = previousTime + (System.currentTimeMillis() - lastLoginTime)

                // Optional safety cap, avoid arbitrary resets
                if (totalTime > 94608000000) totalTime = 94608000000

                PlayerData.playTimeCache[e.player] = totalTime
                PlayerData.playtimeLocal[e.player] = System.currentTimeMillis() // Reset to now
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    private fun setBackLocation(e: PlayerQuitEvent) {
        if (!e.player.hasPermission("totalessentials.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
        if (e.player.location == SpawnData.spawnLocation["spawn"]) return
        PlayerData.backLocation[e.player] = e.player.location
    }

    private fun sendLeaveEmbed(e: PlayerQuitEvent) {
        if (MainConfig.discordbotSendLeaveMessage) {
            DiscordManager.sendDiscordMessage(
                LangConfig.discordchatDiscordSendLeaveMessage.replace("%player%", e.player.name),
                true
            )
        }
    }
}
