package github.gilbertokpl.essentialsk.api.discord

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager.hashTextChannel
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.*
import net.dv8tion.jda.api.EmbedBuilder
import org.bukkit.plugin.java.JavaPlugin

class Discord(pl: JavaPlugin) {

    /**
     * Send message to discord.
     */
    fun sendDiscordMessage(message: String, embed: Boolean) {
        sendDiscordMessage(message, MainConfig.discordbotIdDiscordChat, embed)
    }

    /**
     * Send message to discord with chat ID.
     */
    fun sendDiscordMessage(message: String, chatID: String, embed: Boolean) {
        if (DiscordUtil.jda == null && MainConfig.discordbotConnectDiscordChat) {
            MainUtil.consoleMessage(
                EColor.YELLOW.color + GeneralLang.discordchatNoToken + EColor.RESET.color
            )
            return
        }

        if (hashTextChannel[chatID] == null) {
            TaskUtil.asyncExecutor {
                val newChat = DiscordUtil.setupDiscordChat() ?: return@asyncExecutor

                hashTextChannel[chatID] = newChat

                if (embed) {
                    val msg = EmbedBuilder().setDescription(message).setColor(ColorUtil.randomColor())
                    newChat.sendMessageEmbeds(msg.build()).queue()
                    return@asyncExecutor
                }

                newChat.sendMessage(message).queue()

            }
            return
        }

        try {
            if (embed) {
                val msg = EmbedBuilder().setDescription(message).setColor(ColorUtil.randomColor())
                hashTextChannel[chatID]!!.sendMessageEmbeds(msg.build()).queue()
                return
            }
            hashTextChannel[chatID]!!.sendMessage(message).queue()
        } catch (e: Throwable) {
            hashTextChannel.remove(chatID)
        }
    }

    /**
     * Reload all chat ids.
     */
    fun reloadDiscordChat() {
        hashTextChannel.clear()
    }

    /**
     * Get chat id in use.
     *
     * @return chat in use, null if bot not in use.
     */
    fun getChatIdInUse(): String? {
        return hashTextChannel[MainConfig.discordbotIdDiscordChat]?.id
    }

    /**
     * Check bot usage.
     *
     * @return true if bot is in use.
     */
    fun botInUse(): Boolean {
        return DiscordUtil.jda != null
    }
}