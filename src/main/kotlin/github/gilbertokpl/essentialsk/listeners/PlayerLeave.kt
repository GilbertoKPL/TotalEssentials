package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.api.DiscordAPI
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.data.start.PlayerDataLoader
import github.gilbertokpl.essentialsk.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
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
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            val playerData = PlayerDataV2[e.player] ?: return
            if (!playerData.vanishCache && !e.player.hasPermission("*")) {
                if (MainConfig.messagesLeaveMessage) {
                    MainUtil.serverMessage(
                        GeneralLang.messagesLeaveMessage
                            .replace("%player%", e.player.name)
                    )
                }
                if (MainConfig.discordbotSendLeaveMessage) {
                    sendLeaveEmbed(e)
                }
            }
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
        try {
            PlayerDataLoader.saveCache(e.player)
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun setBackLocation(e: PlayerQuitEvent) {
        if (!e.player.hasPermission("essentialsk.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
        PlayerDataV2[e.player]?.setBack(e.player.location) ?: return
    }

    private fun sendLeaveEmbed(e: PlayerQuitEvent) {

        DiscordAPI.sendMessageDiscord(
            GeneralLang.discordchatDiscordSendLeaveMessage.replace("%player%", e.player.name),
            true
        )
    }
}
