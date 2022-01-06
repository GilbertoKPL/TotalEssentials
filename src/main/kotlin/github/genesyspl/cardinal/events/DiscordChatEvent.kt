package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.PluginUtil
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class DiscordChatEvent : ListenerAdapter() {
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotConnectDiscordChat) {
            if (e.author.isBot) return
            if (e.message.channel.id == github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotIdDiscordChat) {
                var msg = e.message.contentRaw
                e.message.emotes.forEach {
                    msg = msg.replace(it.toString(), "")
                }
                if (msg.length > github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotMaxMessageLenght) {
                    e.channel.sendMessage(
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatMessageNotSendToServer
                            .replace(
                                "%lenght%",
                                github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotMaxMessageLenght.toString()
                            )
                            .replace("%author%", e.member?.asMention!!)
                    ).queue {
                        it.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    return
                }
                PluginUtil.getInstance().serverMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatDiscordToServerPattern
                        .replace("%player%", e.author.name)
                        .replace("%message%", msg)
                )
            }
        }
    }
}