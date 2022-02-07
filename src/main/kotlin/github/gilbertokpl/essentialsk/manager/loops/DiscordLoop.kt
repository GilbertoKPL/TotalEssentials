package github.gilbertokpl.essentialsk.manager.loops

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.*
import java.util.concurrent.TimeUnit

internal object DiscordLoop {

    var start = false

    fun start() {

        if (!MainConfig.discordbotConnectDiscordChat) return

        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            if (MainConfig.discordbotConnectDiscordChat) {
                if (DiscordUtil.jda == null) {
                    MainUtil.consoleMessage(
                        EColor.YELLOW.color + GeneralLang.discordchatNoToken + EColor.RESET.color
                    )
                    return@scheduleWithFixedDelay
                }

                start = true

                DiscordUtil.jda?.getTextChannelById(MainConfig.discordbotIdDiscordChat)?.manager?.setTopic(
                    GeneralLang.discordchatDiscordTopic
                        .replace("%online%", PlayerUtil.getIntOnlinePlayers(false).toString())
                        .replace("%online_time%", TimeUtil.convertMillisToString(TimeUtil.getOnlineTime(), true))
                        .replace("%time%", TimeUtil.getCurrentDate())
                )?.queue()
            } else {
                TaskUtil.restartDiscordExecutor()
                start = false
            }
        }, 2, 10, TimeUnit.MINUTES)
    }
}
