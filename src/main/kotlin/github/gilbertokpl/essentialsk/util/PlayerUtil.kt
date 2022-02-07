package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.SpawnData
import github.gilbertokpl.essentialsk.player.PlayerData
import org.apache.commons.io.IOUtils
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.net.URL

internal object PlayerUtil {

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = ReflectUtil.getPlayers()
        if (!vanish) {
            amount = amount.filter {
                PlayerData[it] != null && !PlayerData[it]!!.vanishCache
            }
        }
        return amount.size
    }

    fun getNumberGamemode(gamemode: GameMode): Int {
        try {
            if (gamemode == GameMode.SURVIVAL) {
                return 0
            }
            if (gamemode == GameMode.CREATIVE) {
                return 1
            }
            if (gamemode == GameMode.ADVENTURE) {
                return 2
            }
            if (gamemode == GameMode.SPECTATOR) {
                return 3
            }
        } catch (e: Throwable) {
            if (gamemode == GameMode.SURVIVAL) {
                return 0
            }
            if (gamemode == GameMode.CREATIVE) {
                return 1
            }
        }
        return 1
    }

    fun getGamemodeNumber(number: String): GameMode {
        return when (number.lowercase()) {
            "0" -> GameMode.SURVIVAL
            "1" -> GameMode.CREATIVE
            "survival" -> GameMode.SURVIVAL
            "creative" -> GameMode.CREATIVE
            "adventure" -> try {
                GameMode.ADVENTURE
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }
            "spectactor" -> try {
                GameMode.SPECTATOR
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }
            "2" -> try {
                GameMode.ADVENTURE
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }
            "3" -> try {
                GameMode.SPECTATOR
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }
            else -> GameMode.SURVIVAL
        }
    }

    fun getPlayerUUID(p: Player): String {
        return if (EssentialsK.instance.server.onlineMode) {
            p.uniqueId.toString()
        } else {
            p.name.lowercase()
        }
    }

    fun getPlayerUUID(p: OfflinePlayer): String {
        return if (EssentialsK.instance.server.onlineMode) {
            val jsonPlayer = JsonParser.parseString(
                IOUtils.toString(
                    URL("https://api.mojang.com/users/profiles/minecraft/${p.name!!.lowercase()}"),
                    "UTF-8"
                )
            ).asJsonObject
            jsonPlayer.get("id").asString
        } else {
            p.name!!.lowercase()
        }
    }

    fun getPlayerUUID(playerName: String): String {
        return if (EssentialsK.instance.server.onlineMode) {
            val jsonPlayer = JsonParser.parseString(
                IOUtils.toString(
                    URL("https://api.mojang.com/users/profiles/minecraft/${playerName.lowercase()}"),
                    "UTF-8"
                )
            ).asJsonObject
            jsonPlayer.get("id").asString
        } else {
            playerName.lowercase()
        }
    }

    fun sendToSpawn(p: Player) {
        try {
            if (MainConfig.spawnSendToSpawnOnLogin) {
                val loc = SpawnData["spawn"] ?: run {
                    if (p.hasPermission("*")) {
                        p.sendMessage(GeneralLang.spawnSendNotSet)
                    }
                    return
                }
                p.teleport(loc)
            }
        }
        catch (ignored : Exception) { }
    }

    fun finishLogin(p: Player, vanishCache: Boolean) {

        if (!vanishCache && !p.hasPermission("*")) {
            if (MainConfig.messagesLoginMessage) {
                MainUtil.serverMessage(
                    GeneralLang.messagesEnterMessage
                        .replace("%player%", p.name)
                )
            }
            if (MainConfig.discordbotSendLoginMessage) {
                EssentialsK.api.getDiscordAPI().sendDiscordMessage(
                    GeneralLang.discordchatDiscordSendLoginMessage.replace("%player%", p.name),
                    true
                )
            }
        }

        if (MainConfig.vanishActivated) {
            if (p.hasPermission("essentialsk.commands.vanish") ||
                p.hasPermission("essentialsk.bypass.vanish")
            ) return
            for (it in ReflectUtil.getPlayers()) {
                if (PlayerData[it]?.vanishCache ?: continue) {
                    @Suppress("DEPRECATION")
                    p.hidePlayer(it)
                }
            }
        }
    }
}
