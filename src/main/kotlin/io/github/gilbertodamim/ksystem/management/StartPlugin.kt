package io.github.gilbertodamim.ksystem.management

import io.github.gilbertodamim.ksystem.commands.StartCommands
import io.github.gilbertodamim.ksystem.config.ConfigMain.startConfig
import io.github.gilbertodamim.ksystem.database.SqlSelector
import io.github.gilbertodamim.ksystem.events.StartEvents
import io.github.gilbertodamim.ksystem.management.Manager.startMaterials

class StartPlugin {
    init {
        startMaterials()
        startConfig()
        SqlSelector()
        StartCommands().start()
        StartEvents().start()
    }
}