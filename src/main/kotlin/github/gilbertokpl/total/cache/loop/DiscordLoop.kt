package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.PlayerUtil

import java.util.concurrent.TimeUnit

object DiscordLoop {

    var start = false
    fun start() {
        if (!MainConfig.discordbotConnectDiscordChat) {
            return
        }

        if (start) return

        start = true

        TotalEssentials.getCore().getTask().getInternalExecutor().scheduleWithFixedDelay({
            val online = PlayerUtil.getIntOnlinePlayers(false)
            val onlineTime = TotalEssentials.getCore().getTime().getOnlineTime()
            val currentTime = TotalEssentials.getCore().getTime().getCurrentDate()

            Discord.jda?.getTextChannelById(MainConfig.discordbotIdDiscordChat)?.manager?.setTopic(
                LangConfig.discordchatDiscordTopic
                    .replace("%online%", online.toString())
                    .replace(
                        "%online_time%",
                        TotalEssentials.getCore().getTime().convertMillisToString(onlineTime, true)
                    )
                    .replace("%time%", currentTime)
            )?.queue()
        }, 15, 15, TimeUnit.MINUTES)
    }
}