package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

object DiscordLoop {

    var start = false
    fun start() {
        if (!MainConfig.discordbotConnectDiscordChat) {
            return
        }

        if (start) return

        start = true

        TaskUtil.getInternalExecutor().scheduleWithFixedDelay({
            val online = PlayerUtil.getIntOnlinePlayers(false)
            val onlineTime = TotalEssentialsJava.basePlugin.getTime().getOnlineTime()
            val currentTime = TotalEssentialsJava.basePlugin.getTime().getCurrentDate()

            Discord.jda?.getTextChannelById(MainConfig.discordbotIdDiscordChat)?.manager?.setTopic(
                LangConfig.discordchatDiscordTopic
                    .replace("%online%", online.toString())
                    .replace(
                        "%online_time%",
                        TotalEssentialsJava.basePlugin.getTime().convertMillisToString(onlineTime, true)
                    )
                    .replace("%time%", currentTime)
            )?.queue()
        }, 15, 15, TimeUnit.MINUTES)
    }
}