package github.gilbertokpl.essentialsk.config.otherConfigs

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.loops.AnnounceLoop
import github.gilbertokpl.essentialsk.manager.loops.DiscordLoop
import github.gilbertokpl.essentialsk.manager.loops.MoneyLoop
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils


internal object OtherConfig {

    var vanishBlockedOtherCmds = HashMap<String, Int>(30)

    var announcementsListAnnounce = HashMap<Int, String>(30)

    var deathmessageListReplacer = HashMap<String, String>(60)

    var serverPrefix = ""

    lateinit var vanish: List<String>

    lateinit var announce: List<String>

    lateinit var deathMessage: List<String>

    lateinit var deathMessageEntity: List<String>

    fun start() {

        EssentialsK.api.getDiscordAPI().reloadDiscordChat()

        try {
            deathmessageListReplacer.clear()

            for (d in deathMessageEntity) {
                val to = d.split("-")
                try {
                    deathmessageListReplacer[to[0].lowercase()] = to[1]
                } catch (ignored: Exception) {
                }
            }
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }

        try {
            for (d in deathMessage) {
                val to = d.split("-")
                try {
                    deathmessageListReplacer[to[0].lowercase()] = to[1]
                } catch (ignored: Exception) {
                }
            }
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }

        try {
            vanishBlockedOtherCmds.clear()

            for (v in vanish) {
                val split = v.split(" ")
                if (split.isEmpty()) {
                    vanishBlockedOtherCmds[v] = 0
                    continue
                }
                var bol = false
                for (i in 0..split.size) {
                    if (split[i] == "<player>") {
                        vanishBlockedOtherCmds[split[0]] = i
                        bol = true
                        break
                    }
                }
                if (bol) {
                    continue
                }
            }
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
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
                    TaskUtil.restartAnnounceExecutor()
                    AnnounceLoop.start(announce.size, MainConfig.announcementsTime)
                }
            }
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }

        if (!MainConfig.discordbotConnectDiscordChat && !MainConfig.discordbotSendTopicUpdate && DiscordLoop.start) {
            TaskUtil.getDiscordExecutor().shutdown()
            DiscordLoop.start = false
        }

        if (MainConfig.discordbotConnectDiscordChat && MainConfig.discordbotSendTopicUpdate && !DiscordLoop.start) {
            DiscordLoop.start()
        }

        MoneyLoop.start()

        EditKitInventory.setup()

        KitGuiInventory.setup()
    }

}
