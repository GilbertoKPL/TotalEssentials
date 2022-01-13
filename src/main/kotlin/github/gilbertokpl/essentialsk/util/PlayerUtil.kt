package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.manager.EColor
import net.dv8tion.jda.api.EmbedBuilder
import org.apache.commons.io.IOUtils
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.net.URL
import java.util.regex.Pattern

object PlayerUtil {

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = ReflectUtil.getPlayers()
        if (!vanish) {
            amount = amount.filter { !(PlayerDataV2[it]!!).vanishCache }
        }
        return amount.size
    }

    fun getNumberGamemode(gamemode: GameMode): Int {
        return when (gamemode) {
            GameMode.SURVIVAL -> 0
            GameMode.CREATIVE -> 1
            GameMode.ADVENTURE -> 2
            GameMode.SPECTATOR -> 3
        }
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
        if (DiscordUtil.jda == null) {
            MainUtil.consoleMessage(
                EColor.YELLOW.color + GeneralLang.discordchatNoToken + EColor.RESET.color
            )
            return
        }
        if (DataManager.discordChat == null) {
            TaskUtil.asyncExecutor {
                val newChat =
                    DiscordUtil.jda!!.getTextChannelById(MainConfig.discordbotIdDiscordChat)
                        ?: run {
                            MainUtil.consoleMessage(
                                EColor.YELLOW.color + GeneralLang.discordchatNoChatId + EColor.RESET.color
                            )
                            return@asyncExecutor
                        }
                DataManager.discordChat = newChat

                DataManager.discordChat!!.sendMessageEmbeds(
                    EmbedBuilder().setDescription(
                        GeneralLang.discordchatDiscordSendLoginMessage.replace("%player%", p.name)
                    ).setColor(MainUtil.randomColor()).build()
                ).complete()
            }
        }

        DataManager.discordChat?.sendMessageEmbeds(
            EmbedBuilder().setDescription(
                GeneralLang.discordchatDiscordSendLoginMessage.replace("%player%", p.name)
            ).setColor(MainUtil.randomColor()).build()
        )?.queue()
    }
}
