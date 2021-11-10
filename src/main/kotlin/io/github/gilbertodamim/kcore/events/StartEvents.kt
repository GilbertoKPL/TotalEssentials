package io.github.gilbertodamim.kcore.events

import io.github.gilbertodamim.kcore.KCoreMain.instance


object StartEvents {
    fun start() {
        closeInventory()
        clickInventory()
        asyncChatEvent()
        joinEvent()
        leaveEvent()
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

    private fun joinEvent() {
        instance.server.pluginManager.registerEvents(PlayerJoinEvent(), instance)
    }

    private fun leaveEvent() {
        instance.server.pluginManager.registerEvents(PlayerLeaveEvent(), instance)
    }
}