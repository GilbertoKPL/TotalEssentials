package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.ColorUtil
import github.gilbertokpl.total.util.MainUtil
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

object DiscordLoop {
    fun setup() {
        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            if (MainConfig.discordbotConnectDiscordChat) {
                if (Discord.jda == null) {
                    MainUtil.consoleMessage(
                        ColorUtil.YELLOW.color + LangConfig.discordchatNoToken + ColorUtil.RESET.color
                    )
                    return@scheduleWithFixedDelay
                }

                Discord.jda?.getTextChannelById(MainConfig.discordbotIdDiscordChat)?.manager?.setTopic(
                    LangConfig.discordchatDiscordTopic
                        .replace("%online%", PlayerUtil.getIntOnlinePlayers(false).toString())
                        .replace("%online_time%", TotalEssentials.basePlugin.getTime().convertMillisToString(TotalEssentials.basePlugin.getTime().getOnlineTime(), true))
                        .replace("%time%", TotalEssentials.basePlugin.getTime().getCurrentDate())
                )?.queue()
            }
        }, 2, 10, TimeUnit.MINUTES)
    }
}