package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.cache.local.PlayerData
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

        LoginData.loggedIn[e.player] = false

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
        try {
            if (MainConfig.playtimeActivated) {
                if (PlayerData.playTimeCache[e.player] != null) {
                    PlayerData.playTimeCache[e.player] =
                        PlayerData.playTimeCache[e.player]!! + System.currentTimeMillis() - (PlayerData.playtimeLocal[e.player]!!)
                } else {
                    PlayerData.playTimeCache[e.player] = 0L
                }
                PlayerData.playtimeLocal[e.player] = 0L
            }
        } catch (e: Throwable) {

        }

    }

    private fun setBackLocation(e: PlayerQuitEvent) {
        if (!e.player.hasPermission("totalessentials.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
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
