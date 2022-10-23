package github.gilbertokpl.total.manager.loops

import  github.gilbertokpl.total.data.OtherConfig
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

internal object AnnounceLoop {

    private var maxValue = 0

    private var locValues = 1

    fun start(values: Int, time: Int) {
        maxValue = values
        locValues = 1

        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({

            val online = PlayerUtil.getIntOnlinePlayers(false)

            github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().getPlayers().forEach {
                it.sendMessage(
                    OtherConfig.announcementsListAnnounce[locValues]!!.replace(
                        "%players_online%",
                        online.toString()
                    )
                )
            }
            if (locValues == maxValue) {
                locValues = 1
            } else {
                locValues += 1
            }
        }, time.toLong(), time.toLong(), TimeUnit.MINUTES)
    }
}
