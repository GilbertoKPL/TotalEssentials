package me.gilberto.essentials.management

import me.gilberto.essentials.config.ConfigMain.startconfig
import me.gilberto.essentials.database.SqlSelector
import me.gilberto.essentials.lib.LibChecker.checkversion

class StartPlugin {
    init {
        startconfig()
        SqlSelector()
    }
}