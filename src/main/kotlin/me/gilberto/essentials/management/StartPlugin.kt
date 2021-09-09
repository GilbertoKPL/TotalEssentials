package me.gilberto.essentials.management

import me.gilberto.essentials.config.ConfigMain.startconfig
import me.gilberto.essentials.database.SqlSelector
import me.gilberto.essentials.version.VersionChecker.checkversion

class StartPlugin {
    init {
        checkversion()
        startconfig()
        SqlSelector()
    }
}