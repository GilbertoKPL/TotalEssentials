package me.gilberto.essentials.management

import me.gilberto.essentials.commands.StartCommands
import me.gilberto.essentials.config.ConfigMain.startconfig
import me.gilberto.essentials.database.SqlSelector
import me.gilberto.essentials.events.StartEvents

class StartPlugin {
    init {
        startconfig()
        SqlSelector()
        StartCommands().start()
        StartEvents().start()
    }
}