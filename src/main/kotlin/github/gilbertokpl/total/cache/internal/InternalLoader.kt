package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.cache.internal.inventory.EditKit
import github.gilbertokpl.total.cache.internal.inventory.Kit
import github.gilbertokpl.total.cache.loop.AnnounceLoop
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil


internal object InternalLoader {

    var announcementsListAnnounce = HashMap<Int, String>(30)

    var deathMessageListReplacer = HashMap<String, String>(60)

    fun start(announce: List<String>, deathMessage: List<String>, deathMessageEntity: List<String>) {

        try {
            deathMessageListReplacer.clear()

            for (d in deathMessageEntity) {
                val to = d.split("-")
                try {
                    deathMessageListReplacer[to[0].lowercase()] = to[1]
                } catch (ignored: Exception) {
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        try {
            for (d in deathMessage) {
                val to = d.split("-")
                try {
                    deathMessageListReplacer[to[0].lowercase()] = to[1]
                } catch (ignored: Exception) {
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        try {
            var dif = false

            val hash = HashMap<Int, String>()

            var to = 1

            for (a in announce) {
                hash[to] = a
                to += 1
            }

            if (hash.size != announcementsListAnnounce.size) {
                dif = true
            } else {
                for (i in hash) {
                    if (announcementsListAnnounce[i.key] != i.value) {
                        dif = true
                        break
                    }
                }
            }

            if (dif) {
                announcementsListAnnounce = hash
                if (MainConfig.announcementsEnabled) {
                    TaskUtil.restartInternalExecutor()
                    AnnounceLoop.start(
                        announce.size,
                        MainConfig.announcementsTime
                    )
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        EditKit.setup()

        Kit.setup()
    }

}
