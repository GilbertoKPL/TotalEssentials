package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.ColorUtil
import github.gilbertokpl.total.util.MainUtil
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object DiscordLoop {
    fun start() {
        if (!MainConfig.discordbotConnectDiscordChat) {
            return
        }

        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            val online = PlayerUtil.getIntOnlinePlayers(false)
            val onlineTime = TotalEssentials.basePlugin.getTime().getOnlineTime()
            val currentTime = TotalEssentials.basePlugin.getTime().getCurrentDate()

            Discord.jda?.getTextChannelById(MainConfig.discordbotIdDiscordChat)?.manager?.setTopic(
                LangConfig.discordchatDiscordTopic
                    .replace("%online%", online.toString())
                    .replace("%online_time%", TotalEssentials.basePlugin.getTime().convertMillisToString(onlineTime, true))
                    .replace("%time%", currentTime)
            )?.queue()
        }, 15, 15, TimeUnit.MINUTES)
    }
}