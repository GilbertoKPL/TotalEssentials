package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.manager.IInstance

class OtherConfigUtil {

    fun start() {
        val config = ConfigUtil.getInstance().configList["CommandsConfig"]!!

        val vanish = ConfigUtil.getInstance().getStringList(config, "vanish.blocked-other-cmds", false)

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
    }


    companion object : IInstance<OtherConfigUtil> {
        private val instance = createInstance()
        override fun createInstance(): OtherConfigUtil = OtherConfigUtil()
        override fun getInstance(): OtherConfigUtil {
            return instance
        }
    }
}