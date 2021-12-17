package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.io.IOUtils
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.net.URL

class PlayerUtil {

    fun getGamemodeNumber(number: String) : GameMode{
        return when (number) {
            "0" -> GameMode.SURVIVAL
            "1" -> GameMode.CREATIVE
            "2" -> try {
                GameMode.ADVENTURE
            } catch (e: Exception) {
                GameMode.SURVIVAL
            }
            "3" -> try {
                GameMode.SPECTATOR
            } catch (e: Exception) {
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

    companion object : IInstance<PlayerUtil> {
        private val instance = createInstance()
        override fun createInstance(): PlayerUtil = PlayerUtil()
        override fun getInstance(): PlayerUtil {
            return instance
        }
    }
}