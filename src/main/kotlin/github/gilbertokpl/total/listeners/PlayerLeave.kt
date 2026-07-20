package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import github.gilbertokpl.total.login.VelocityAuthBridge
import github.gilbertokpl.total.util.PlayerUtil.getMojangSkinURL
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {

    companion object {
        private const val MAX_PLAYTIME_MS = 94608000000L // ~3 anos em milissegundos
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = null

        val player = event.player
        LoginData.isLoggedIn[player] = false
        VelocityAuthBridge.clear(player)

        if (MainConfig.backActivated) {
            updateBackLocation(event)
        }

        handleLeaveMessages(event)

        if (MainConfig.playtimeActivated) {
            updatePlaytime(event)
        }
    }

    private fun updateBackLocation(event: PlayerQuitEvent) {
        val player = event.player

        if (!player.hasPermission("totalessentials.commands.back")) return
        if (MainConfig.backDisabledWorlds.contains(player.world.name.lowercase())) return
        if (player.location == SpawnData.spawnLocation["spawn"]) return

        PlayerData.backLocation[player] = player.location
    }

    private fun handleLeaveMessages(event: PlayerQuitEvent) {
        val player = event.player
        val isVanished = PlayerData.vanishCache[player] ?: false

        // Não envia mensagem se estiver vanish ou for admin (permissão *)
        if (isVanished || player.hasPermission("*")) return

        if (MainConfig.messagesLeaveMessage) {
            ServerUtil.broadcastMessage(
                LangConfig.messagesLeaveMessage.replace("%player%", player.name)
            )
        }

        if (MainConfig.discordbotSendLeaveMessage) {
            sendLeaveEmbed(event)
        }
    }

    private fun updatePlaytime(event: PlayerQuitEvent) {
        val player = event.player

        val loginTime = PlayerData.playtimeLocal[player] ?: System.currentTimeMillis()
        val previousPlaytime = PlayerData.playTimeCache[player] ?: 0L

        val sessionDuration = System.currentTimeMillis() - loginTime
        var totalPlaytime = previousPlaytime + sessionDuration

        // Limita o playtime máximo para evitar overflow
        if (totalPlaytime > MAX_PLAYTIME_MS) {
            totalPlaytime = MAX_PLAYTIME_MS
        }

        PlayerData.playTimeCache[player] = totalPlaytime
    }

    private fun sendLeaveEmbed(event: PlayerQuitEvent) {
        DiscordManager.sendDiscordMessage(
            message = LangConfig.discordchatDiscordSendLeaveMessage.replace("%player%", event.player.name),
            embed = true,
            tittle = true,
            avatarUrl = getMojangSkinURL(event.player)
        )
    }
}
