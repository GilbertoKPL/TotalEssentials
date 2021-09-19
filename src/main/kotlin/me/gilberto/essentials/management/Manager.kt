package me.gilberto.essentials.management

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.EssentialsMain.pluginName

object Manager {
    fun consoleMessage(msg: String) {
        instance.server.consoleSender.sendMessage("$pluginName $msg")
    }

    fun pluginpastedir(): String = instance.dataFolder.path
    fun pluginlangdir(): String = instance.dataFolder.path + "/lang/"
    fun disableplugin() {
        consoleMessage("§cDesabilitando plugin por algum motivo interno")
        instance.server.pluginManager.disablePlugin(instance.server.pluginManager.getPlugin(instance.name))
    }
}