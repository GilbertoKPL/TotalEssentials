package io.github.gilbertodamim.essentials.events

import io.github.gilbertodamim.essentials.EssentialsMain.instance


class StartEvents {
    fun start() {
        closeInventory()
    }

    private fun closeInventory() {
        instance.server.pluginManager.registerEvents(CloseInventoryEvent(), instance)
    }
}