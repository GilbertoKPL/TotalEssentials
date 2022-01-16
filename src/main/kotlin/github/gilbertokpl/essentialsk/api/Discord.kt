package github.gilbertokpl.essentialsk.api

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.ColorUtil
import github.gilbertokpl.essentialsk.util.DiscordUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA

object Discord {

    fun sendMessageDiscord(message: String, embed: Boolean = false) {
        if (DiscordUtil.jda == null) {
            MainUtil.consoleMessage(
                EColor.YELLOW.color + GeneralLang.discordchatNoToken + EColor.RESET.color
            )
            return
        }
        if (DataManager.discordChat == null) {
            TaskUtil.asyncExecutor {
                val newChat = DiscordUtil.setupDiscordChat() ?: return@asyncExecutor

                if (embed) {
                    val msg = EmbedBuilder().setDescription(message).setColor(ColorUtil.randomColor())
                    newChat.sendMessageEmbeds(msg.build()).queue()
                    return@asyncExecutor
                }

                newChat.sendMessage(message).queue()

            }
            return
        }

        if (embed) {
            val msg = EmbedBuilder().setDescription(message).setColor(ColorUtil.randomColor())
            DataManager.discordChat!!.sendMessageEmbeds(msg.build()).queue()
            return
        }

        DataManager.discordChat!!.sendMessage(message).queue()

    }

    fun getJDA(): JDA? {
        return DiscordUtil.jda
    }
}