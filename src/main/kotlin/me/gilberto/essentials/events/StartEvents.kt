package me.gilberto.essentials.events

import me.gilberto.essentials.EssentialsMain.instance


class StartEvents {
    fun start() {
        closeinventory()
    }
    private fun closeinventory() {
        instance.server.pluginManager.registerEvents(CloseInventoryEvent(), instance)
    }
}