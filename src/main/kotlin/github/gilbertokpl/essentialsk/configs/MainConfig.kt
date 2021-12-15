package github.gilbertokpl.essentialsk.configs

import github.gilbertokpl.essentialsk.manager.IInstance

class MainConfig {

    var generalSelectedLang: String = "pt_BR"

    var databaseType: String = ""

    var databaseSqlIp: String = ""

    var databaseSqlPort: String = ""

    var databaseSqlUsername: String = ""

    var databaseSqlDatabase: String = ""

    var databaseSqlPassword: String = ""

    var kitsActivated: Boolean = true

    var kitsUseShortTime: Boolean = false

    var kitsDropItemsInCatch: Boolean = false

    var kitsEquipArmorInCatch: Boolean = true

    var nicksActivated: Boolean = true

    var nicksCanPlayerHaveSameNick: Boolean = false

    var nicksBlockedNicks: List<String> = emptyList()

    var homesActivated: Boolean = true

    var homesDefaultLimitHomes: Int = 0

    var homesTimeToTeleport: Int = 0

    var homesBlockWorlds: List<String> = emptyList()

    var warpsActivated: Boolean = true

    var warpsTimeToTeleport: Int = 0

    var tpActivated: Boolean = true

    var tpaActivated: Boolean = true

    var tpaTimeToAccept: Int = 0

    var tpaTimeToTeleport: Int = 0

    var echestActivated: Boolean = true

    var gamemodeActivated: Boolean = true

    var vanishActivated: Boolean = true

    var feedActivated: Boolean = true

    var feedNeedEatBelow: Boolean = false

    var healActivated: Boolean = true

    var healNeedHealBelow: Boolean = false

    var lightActivated: Boolean = true

    var backActivated: Boolean = true

    var backDisabledWorlds: List<String> = emptyList()

    companion object : IInstance<MainConfig> {
        private val instance = createInstance()
        override fun createInstance(): MainConfig = MainConfig()
        override fun getInstance(): MainConfig {
            return instance
        }
    }
}