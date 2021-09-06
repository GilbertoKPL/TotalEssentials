package me.gilberto.essentials.management

import me.gilberto.essentials.EssentialsInstance.instance

object Manager {
    fun consoleMessage(msg: String) {
        instance.server.consoleSender.sendMessage(msg)
    }
    fun pluginpastedir() : String = instance.dataFolder.path
}