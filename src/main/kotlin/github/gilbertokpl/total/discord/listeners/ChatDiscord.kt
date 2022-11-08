package github.gilbertokpl.total.discord.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MainUtil
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class ChatDiscord : ListenerAdapter() {
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (MainConfig.discordbotConnectDiscordChat) {
            if (e.author.isBot) return
            if (e.message.channel.id == MainConfig.discordbotIdDiscordChat) {
                var msg = e.message.contentRaw
                e.message.stickers.forEach {
                    msg = msg.replace(it.toString(), "")
                }
                if (msg.length > MainConfig.discordbotMaxMessageLenght) {
                    e.channel.sendMessage(
                        LangConfig.discordchatMessageNotSendToServer
                            .replace("%lenght%", MainConfig.discordbotMaxMessageLenght.toString())
                            .replace("%author%", e.member?.asMention!!)
                    ).queue {
                        it.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    return
                }
                MainUtil.serverMessage(
                    LangConfig.discordchatDiscordToServerPattern
                        .replace("%player%", e.author.name)
                        .replace("%message%", msg)
                )
            }
        }
    }
}
