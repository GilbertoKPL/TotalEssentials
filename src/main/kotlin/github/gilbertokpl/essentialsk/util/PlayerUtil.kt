package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.manager.IInstance
import net.dv8tion.jda.api.EmbedBuilder
import org.apache.commons.io.IOUtils
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.net.URL

class PlayerUtil {

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = ReflectUtil.getInstance().getPlayers()
        if (!vanish) {
            amount =
                amount.filter { DataManager.getInstance().playerCacheV2[it.name.lowercase()]?.vanishCache ?: false }
        }
        return amount.size
    }

    fun getGamemodeNumber(number: String): GameMode {
        return when (number) {
            "0" -> GameMode.SURVIVAL
            "1" -> GameMode.CREATIVE
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
        if (DiscordUtil.getInstance().jda == null) {
            PluginUtil.getInstance().consoleMessage(
                EColor.YELLOW.color + GeneralLang.getInstance().discordchatNoToken + EColor.RESET.color
            )
            return
        }
        if (DataManager.getInstance().discordChat == null) {
            TaskUtil.getInstance().asyncExecutor {
                val newChat =
                    DiscordUtil.getInstance().jda!!.getTextChannelById(MainConfig.getInstance().discordbotIdDiscordChat)
                        ?: run {
                            PluginUtil.getInstance().consoleMessage(
                                EColor.YELLOW.color + GeneralLang.getInstance().discordchatNoChatId + EColor.RESET.color
                            )
                            return@asyncExecutor
                        }
                DataManager.getInstance().discordChat = newChat

                DataManager.getInstance().discordChat!!.sendMessageEmbeds(
                    EmbedBuilder().setDescription(
                        GeneralLang.getInstance().discordchatDiscordSendLoginMessage.replace("%player%", p.name)
                    ).setColor(PluginUtil.getInstance().randomColor()).build()
                ).complete()
            }
        }

        DataManager.getInstance().discordChat?.sendMessageEmbeds(
            EmbedBuilder().setDescription(
                GeneralLang.getInstance().discordchatDiscordSendLoginMessage.replace("%player%", p.name)
            ).setColor(PluginUtil.getInstance().randomColor()).build()
        )?.queue()
    }

    companion object : IInstance<PlayerUtil> {
        private val instance = createInstance()
        override fun createInstance(): PlayerUtil = PlayerUtil()
        override fun getInstance(): PlayerUtil {
            return instance
        }
    }
}