package io.github.gilbertodamim.ksystem.events

import io.github.gilbertodamim.ksystem.KSystemMain.instance


class StartEvents {
    fun start() {
        closeInventory()
    }

    private fun closeInventory() {
        instance.server.pluginManager.registerEvents(CloseInventoryEvent(), instance)
    }
}