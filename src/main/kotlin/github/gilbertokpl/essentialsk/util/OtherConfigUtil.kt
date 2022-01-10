package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.loops.Announcements
import org.apache.commons.lang3.exception.ExceptionUtils

object OtherConfigUtil {

    fun start() {

        if (DataManager.discordChat != null) {
            DataManager.discordChat = null
        }

        val vanish = ConfigUtil
            .getStringList(ConfigUtil.configYaml, "vanish.blocked-other-cmds", false)

        val announce = ConfigUtil
            .getStringList(ConfigUtil.configYaml, "announcements.list-announce", true)

        val deathMessage = ConfigUtil
            .getStringList(ConfigUtil.langYaml, "deathmessages.cause-replacer", true)

        val deathMessageEntity = ConfigUtil
            .getStringList(ConfigUtil.langYaml, "deathmessages.entity-replacer", true)

        try {
            OtherConfig.deathmessageListReplacer.clear()

            for (d in deathMessageEntity) {
                val to = d.split("-")
                try {
                    OtherConfig.deathmessageListReplacer[to[0].lowercase()] = to[1]
                } catch (ignored: Exception) {
                }
            }
        } catch (e: Exception) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }

        try {
            for (d in deathMessage) {
                val to = d.split("-")
                try {
                    OtherConfig.deathmessageListReplacer[to[0].lowercase()] = to[1]
                } catch (ignored: Exception) {
                }
            }
        } catch (e: Exception) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }

        try {
            OtherConfig.vanishBlockedOtherCmds.clear()

            for (v in vanish) {
                val split = v.split(" ")
                if (split.isEmpty()) {
                    OtherConfig.vanishBlockedOtherCmds[v] = 0
                    continue
                }
                var bol = false
                for (i in 0..split.size) {
                    if (split[i] == "<player>") {
                        OtherConfig.vanishBlockedOtherCmds[split[0]] = i
                        bol = true
                        break
                    }
                }
                if (bol) {
                    continue
                }
            }
        } catch (e: Exception) {
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

            if (hash.size != OtherConfig.announcementsListAnnounce.size) {
                dif = true
            } else {
                for (i in hash) {
                    if (OtherConfig.announcementsListAnnounce[i.key] != i.value) {
                        dif = true
                        break
                    }
                }
            }

            if (dif) {
                OtherConfig.announcementsListAnnounce = hash
                if (MainConfig.announcementsEnabled) {
                    TaskUtil.restartAnnounceExecutor()
                    Announcements.start(announce.size, MainConfig.announcementsTime)
                }
            }
        } catch (e: Exception) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
    }
}
