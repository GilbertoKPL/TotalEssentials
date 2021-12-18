package github.gilbertokpl.essentialsk.configs

import github.gilbertokpl.essentialsk.manager.IInstance

class OtherConfig {

    var vanishBlockedOtherCmds = HashMap<String, Int>(30)

    var announcementsListAnnounce = HashMap<Int, String>(30)

    companion object : IInstance<OtherConfig> {
        private val instance = createInstance()
        override fun createInstance(): OtherConfig = OtherConfig()
        override fun getInstance(): OtherConfig {
            return instance
        }
    }
}