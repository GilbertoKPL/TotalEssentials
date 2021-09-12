package me.gilberto.essentials.management

import me.gilberto.essentials.config.ConfigMain.startconfig
import me.gilberto.essentials.database.SqlSelector

class StartPlugin {
    init {
        startconfig()
        SqlSelector()
    }
}