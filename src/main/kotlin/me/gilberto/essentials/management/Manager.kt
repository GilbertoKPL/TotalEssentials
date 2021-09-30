package me.gilberto.essentials.management

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.EssentialsMain.pluginName
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Time
import me.gilberto.essentials.config.configs.langs.Time.timedayshort
import me.gilberto.essentials.config.configs.langs.Time.timehourshort
import me.gilberto.essentials.config.configs.langs.Time.timeminuteshort
import me.gilberto.essentials.config.configs.langs.Time.timesecondshort

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

    fun convertmilisstring(to: Long, short: Boolean) : String {
        val seconds = to / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        if (!short) {
            return "$days $timedayshort, ${hours % 24} $$timehourshort, ${minutes % 60} $timeminuteshort ${seconds % 60} $timesecondshort"
        }
        val uniseconds = if (seconds == 1L) { Time.timesecond } else Time.timeseconds
        val uniminutes = if (minutes == 1L) { Time.timeminute } else Time.timeminutes
        val unihours = if (hours == 1L) { Time.timehour } else Time.timehours
        val unidays = if (days == 1L) { Time.timeday } else Time.timedays
        return "$days $unidays, ${hours % 24} $unihours ${minutes % 60} $uniminutes ${seconds % 60} $uniseconds"
    }

}