package io.github.gilbertodamim.kcore.management

import io.github.gilbertodamim.kcore.commands.StartCommands
import io.github.gilbertodamim.kcore.config.ConfigMain.startConfig
import io.github.gilbertodamim.kcore.database.SqlSelector
import io.github.gilbertodamim.kcore.events.StartEvents
import io.github.gilbertodamim.kcore.management.Manager.startMaterials

class StartPlugin {
    init {
        startMaterials()
        startConfig()
        SqlSelector()
        StartCommands().start()
        StartEvents().start()
    }
}