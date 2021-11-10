package io.github.gilbertodamim.kcore.management

import io.github.gilbertodamim.kcore.commands.StartCommands
import io.github.gilbertodamim.kcore.config.ConfigMain
import io.github.gilbertodamim.kcore.database.SqlSelector
import io.github.gilbertodamim.kcore.events.StartEvents
import io.github.gilbertodamim.kcore.management.Manager.startMaterials

class StartPlugin {
    init {
        startMaterials()
        ConfigMain.start()
        SqlSelector()
        StartCommands.start()
        StartEvents.start()
    }
}