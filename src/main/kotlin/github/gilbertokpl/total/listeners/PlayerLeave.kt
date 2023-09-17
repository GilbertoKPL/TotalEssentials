package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.MainUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerQuitEvent) {
        e.quitMessage = null

        LoginData.isLoggedIn[e.player] = false

        if (MainConfig.backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Throwable) {
                e.printStackTrace()
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
            e.printStackTrace()
        }
        try {
            if (MainConfig.playtimeActivated) {
                if (PlayerData.playTimeCache[e.player] != null) {

                    val time = PlayerData.playTimeCache[e.player] ?: 0L

                    val timePlayed = PlayerData.playtimeLocal[e.player] ?: return

                    var newTime = time + (System.currentTimeMillis() - timePlayed)

                    if (time > 94608000000) {
                        newTime = time
                    }

                    if (newTime > 94608000000) {
                        newTime = 518400000
                    }

                    PlayerData.playTimeCache[e.player] = newTime
                } else {
                    PlayerData.playTimeCache[e.player] = 0L
                }
                PlayerData.playtimeLocal[e.player] = 0L
            }
        } catch (e: Throwable) {
            e.printStackTrace()
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
            Discord.sendDiscordMessage(
                LangConfig.discordchatDiscordSendLeaveMessage.replace("%player%", e.player.name),
                true
            )
        }
    }
}
