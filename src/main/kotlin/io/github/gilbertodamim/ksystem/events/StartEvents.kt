package io.github.gilbertodamim.ksystem.events

import io.github.gilbertodamim.ksystem.KSystemMain.instance


class StartEvents {
    fun start() {
        closeInventory()
        clickInventory()
        asyncChatEvent()
    }

    private fun asyncChatEvent() {
        instance.server.pluginManager.registerEvents(PlayerAsyncChatEvent(), instance)
    }

    private fun closeInventory() {
        instance.server.pluginManager.registerEvents(CloseInventoryEvent(), instance)
    }

    private fun clickInventory() {
        instance.server.pluginManager.registerEvents(ClickInventoryEvent(), instance)
    }
}