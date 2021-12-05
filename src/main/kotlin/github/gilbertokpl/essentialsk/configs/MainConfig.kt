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

    companion object : IInstance<MainConfig> {
        private val instance = createInstance()
        override fun createInstance(): MainConfig = MainConfig()
        override fun getInstance(): MainConfig {
            return instance
        }
    }
}