package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.LangConfig.titleJoinSubtitle
import github.gilbertokpl.total.config.files.LangConfig.titleJoinTitle
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import github.gilbertokpl.total.login.LoginManager
import github.gilbertokpl.total.util.ServerUtil
import github.gilbertokpl.total.util.PermissionUtil
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.PlayerUtil.getMojangSkinURL
import github.gilbertokpl.total.util.PlayerUtil.sound
import github.gilbertokpl.total.util.PlayerUtil.title
import github.gilbertokpl.total.vip.VipManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null

        val player = event.player
        val address = player.address?.address?.toString() ?: return

        handleAuthentication(player, address)
        SpawnData.teleportToSpawn(player)

        val task = TotalEssentials.getCore().getTask()

        //title

        if (MainConfig.titleActivated) {
            player.title(titleJoinTitle, titleJoinSubtitle)
        }

        //sound

        if (MainConfig.soundActivated) {
            player.sound(LangConfig.soundJoin)
        }

        task.async {
            initializePlayerData(player)
            handlePlaytime(player)
            sendJoinMessages(player)
            VipManager.checkVip(player.name.lowercase())

            if (MainConfig.generalAntiVpn) {
                PlayerData.playerInfo[player.name, PlayerUtil.checkPlayerIP(address)] = true
            }

            task.sync {
                PlayerData.applyPlayerSettings(player)
            }
        }
    }

    private fun handleAuthentication(player: Player, address: String) {
        if (!MainConfig.authActivated) {
            LoginData.isLoggedIn[player] = true
            return
        }

        LoginData.loginAttempts[player] = 0
        LoginData.values[player] = 0

        if (LoginData.ipAddress[player] == address) {
            player.sendMessage(LangConfig.authAutoLogin)
            LoginData.isLoggedIn[player] = true
        } else {
            LoginManager.loginMessage(player)
        }
    }

    private fun handlePlaytime(player: Player) {
        if (!MainConfig.playtimeActivated) return

        PlayerData.playtimeLocal[player.name, System.currentTimeMillis()] = true
    }

    private fun initializePlayerData(player: Player) {
        if (!PlayerData.checkIfPlayerExists(player)) {
            PlayerData.createNewPlayerData(player.name)
        }

        val homeLimit = PermissionUtil.getNumberPermission(
            player,
            "totalessentials.commands.sethome.",
            MainConfig.homesDefaultLimitHomes
        )
        PlayerData.homeLimitCache[player.name, homeLimit] = true
    }

    private fun sendJoinMessages(player: Player) {
        // Não envia mensagem para admins (permissão *)
        if (player.hasPermission("*")) return

        val isVanished = PlayerData.vanishCache[player] ?: false
        if (isVanished) return

        if (MainConfig.messagesLoginMessage) {
            ServerUtil.broadcastMessage(
                LangConfig.messagesEnterMessage.replace("%player%", player.name)
            )
        }

        if (MainConfig.discordbotSendLoginMessage) {
            DiscordManager.sendDiscordMessage(
                message = LangConfig.discordchatDiscordSendLoginMessage.replace("%player%", player.name),
                embed = true,
                tittle = true,
                avatarUrl = getMojangSkinURL(player)
            )
        }
    }
}