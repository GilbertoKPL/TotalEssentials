package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.api.DiscordAPI
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import org.apache.commons.io.IOUtils
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.net.URL

object PlayerUtil {

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = ReflectUtil.getPlayers()
        if (!vanish) {
            amount = amount.filter {
                PlayerDataV2[it] != null && !PlayerDataV2[it]!!.vanishCache
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

    fun sendLoginEmbed(p: Player) {
        DiscordAPI.sendMessageDiscord(
            GeneralLang.discordchatDiscordSendLoginMessage.replace("%player%", p.name),
            true
        )
    }
}
