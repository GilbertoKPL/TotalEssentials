package github.gilbertokpl.essentialsk.api

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.TextChannel

object DiscordAPI {

    private var discordChat: TextChannel? = null

    fun sendMessageDiscord(message: String, embed: Boolean = false) {
        if (DiscordUtil.jda == null && MainConfig.discordbotConnectDiscordChat) {
            MainUtil.consoleMessage(
                EColor.YELLOW.color + GeneralLang.discordchatNoToken + EColor.RESET.color
            )
            return
        }
        if (discordChat == null) {
            TaskUtil.asyncExecutor {
                val newChat = DiscordUtil.setupDiscordChat() ?: return@asyncExecutor

                discordChat = newChat

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
                discordChat!!.sendMessageEmbeds(msg.build()).queue()
                return
            }
            discordChat!!.sendMessage(message).queue()
        } catch (e: Throwable) {
            discordChat = null
        }
    }

    fun getJDA(): JDA? {
        return DiscordUtil.jda
    }

    fun reloadDiscordChat() {
        if (discordChat != null) {
            discordChat = null
        }
    }

    fun getTextChannel() : TextChannel? {
        return discordChat
    }

    fun getChatIdInUse() : String? {
        return discordChat?.id
    }

    fun botInUse() : Boolean {
        return DiscordUtil.jda != null
    }
}