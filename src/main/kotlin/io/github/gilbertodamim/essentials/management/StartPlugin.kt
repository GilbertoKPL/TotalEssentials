package io.github.gilbertodamim.essentials.management

import io.github.gilbertodamim.essentials.commands.StartCommands
import io.github.gilbertodamim.essentials.config.ConfigMain.startConfig
import io.github.gilbertodamim.essentials.database.SqlSelector
import io.github.gilbertodamim.essentials.events.StartEvents

class StartPlugin {
    init {
        startConfig()
        SqlSelector()
        StartCommands().start()
        StartEvents().start()
    }
}