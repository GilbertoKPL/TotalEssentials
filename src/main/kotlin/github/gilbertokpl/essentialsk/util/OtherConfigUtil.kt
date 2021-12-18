package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.loops.Announcements
import github.gilbertokpl.essentialsk.manager.IInstance

class OtherConfigUtil {

    fun start() {
        val commandsConfig = ConfigUtil.getInstance().configList["CommandsConfig"]!!

        val resorcesConfig = ConfigUtil.getInstance().configList["ResourcesConfig"]!!

        val vanish = ConfigUtil.getInstance().getStringList(commandsConfig, "vanish.blocked-other-cmds", false)

        val announce = ConfigUtil.getInstance().getStringList(resorcesConfig, "announcements.list-announce", true)

        OtherConfig.getInstance().vanishBlockedOtherCmds.clear()

        for (v in vanish) {
            val split = v.split(" ")
            var bol = false
            for (i in 0..split.size) {
                if (split[i] == "<player>") {
                    OtherConfig.getInstance().vanishBlockedOtherCmds[split[0]] = i
                    bol = true
                    break
                }
            }
            if (bol) {
                continue
            }
        }

        var dif = false

        val hash = HashMap<Int, String>()

        var to = 1

        for (a in announce) {
            hash[to] = a
            to += 1
        }

        if (hash.size != OtherConfig.getInstance().announcementsListAnnounce.size) {
            dif = true
        } else {
            for (i in hash) {
                if (OtherConfig.getInstance().announcementsListAnnounce[i.key] != i.value) {
                    dif = true
                    break
                }
            }
        }

        if (dif) {
            OtherConfig.getInstance().announcementsListAnnounce = hash
            if (MainConfig.getInstance().announcementsEnabled) {
                TaskUtil.getInstance().restartAnnounceExecutor()
                Announcements.getInstance().start(announce.size, MainConfig.getInstance().announcementsTime)
            }
        }
    }


    companion object : IInstance<OtherConfigUtil> {
        private val instance = createInstance()
        override fun createInstance(): OtherConfigUtil = OtherConfigUtil()
        override fun getInstance(): OtherConfigUtil {
            return instance
        }
    }
}