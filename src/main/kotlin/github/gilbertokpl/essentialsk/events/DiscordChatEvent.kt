package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.PluginUtil
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class DiscordChatEvent : ListenerAdapter() {
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (MainConfig.getInstance().discordbotConnectDiscordChat) {
            if (e.author.isBot) return
            if (e.message.channel.id == MainConfig.getInstance().discordbotIdDiscordChat) {
                val msg = e.message.contentRaw
                if (msg.length > MainConfig.getInstance().discordbotMaxMessageLenght) {
                    e.channel.sendMessage(
                        GeneralLang.getInstance().discordchatMessageNotSendToServer
                            .replace("%lenght%", MainConfig.getInstance().discordbotMaxMessageLenght.toString())
                            .replace("%author%", e.member?.asMention!!)
                    ).queue {
                        it.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    return
                }
                PluginUtil.getInstance().serverMessage(
                    GeneralLang.getInstance().discordchatDiscordToServerPattern
                        .replace("%player%", e.author.name)
                        .replace("%message%", msg)
                )
            }
        }
    }
}