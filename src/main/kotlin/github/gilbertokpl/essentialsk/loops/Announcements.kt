package github.gilbertokpl.essentialsk.loops

import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import java.util.concurrent.TimeUnit

class Announcements {

    private var maxValue = 0

    private var locValues = 1

    fun start(values: Int, time: Int) {
        maxValue = values
        locValues = 1

        TaskUtil.getInstance().getAnnounceExecutor().scheduleWithFixedDelay({

            val online = PlayerUtil.getInstance().getIntOnlinePlayers(false)

            ReflectUtil.getInstance().getPlayers().forEach {
                it.sendMessage(
                    OtherConfig.getInstance().announcementsListAnnounce[locValues]!!.replace(
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

    companion object : IInstance<Announcements> {
        private val instance = createInstance()
        override fun createInstance(): Announcements = Announcements()
        override fun getInstance(): Announcements {
            return instance
        }
    }
}