package github.gilbertokpl.total.listeners

import github.gilbertokpl.core.external.task.SynchronizationContext
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerJoinEvent) {
        e.joinMessage = null

        val address = e.player.address?.address.toString()

        if (MainConfig.authActivated) {
            LoginData.loginAttempts[e.player] = 0
            LoginData.values[e.player] = 0

            if (LoginData.ipAddress[e.player] == address) {
                e.player.sendMessage(LangConfig.authAutoLogin)
                LoginData.isLoggedIn[e.player] = true
            } else {
                LoginUtil.loginMessage(e.player)
            }
        } else {
            LoginData.isLoggedIn[e.player] = true
        }

        val p = e.player

        SpawnData.teleportToSpawn(p)

        TotalEssentialsJava.basePlugin.getTask().async {

            if (MainConfig.playtimeActivated) {
                PlayerData.playtimeLocal[p] = System.currentTimeMillis()
            }

            waitFor(5)

            val limitHome: Int = PermissionUtil.getNumberPermission(
                p,
                "totalessentials.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )


            if (!PlayerData.checkIfPlayerExist(p)) {
                PlayerData.createNewPlayerData(e.player.name)
            }

            PlayerData.homeLimitCache[p] = limitHome

            if (!p.hasPermission("*")) {
                if (MainConfig.messagesLoginMessage) {
                    MainUtil.serverMessage(
                        LangConfig.messagesEnterMessage
                            .replace("%player%", p.name)
                    )
                }
                if (MainConfig.discordbotSendLoginMessage) {
                    Discord.sendDiscordMessage(
                        LangConfig.discordchatDiscordSendLoginMessage.replace("%player%", p.name),
                        true
                    )
                }
            }

            VipUtil.checkVip(p.name.lowercase())

            if (MainConfig.generalAntiVpn) {
                PlayerData.playerInfo[p] = PlayerUtil.checkPlayer(address)
            }

            switchContext(SynchronizationContext.SYNC)

            PlayerData.values(e.player)

            SpawnData.teleportToSpawn(p)

        }
    }
}
