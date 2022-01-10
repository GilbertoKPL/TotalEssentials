package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.MainUtil
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class ChatDiscord : ListenerAdapter() {
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (MainConfig.discordbotConnectDiscordChat) {
            if (e.author.isBot) return
            if (e.message.channel.id == MainConfig.discordbotIdDiscordChat) {
                var msg = e.message.contentRaw
                e.message.emotes.forEach {
                    msg = msg.replace(it.toString(), "")
                }
                if (msg.length > MainConfig.discordbotMaxMessageLenght) {
                    e.channel.sendMessage(
                        GeneralLang.discordchatMessageNotSendToServer
                            .replace("%lenght%", MainConfig.discordbotMaxMessageLenght.toString())
                            .replace("%author%", e.member?.asMention!!)
                    ).queue {
                        it.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    return
                }
                MainUtil.serverMessage(
                    GeneralLang.discordchatDiscordToServerPattern
                        .replace("%player%", e.author.name)
                        .replace("%message%", msg)
                )
            }
        }
    }
}
