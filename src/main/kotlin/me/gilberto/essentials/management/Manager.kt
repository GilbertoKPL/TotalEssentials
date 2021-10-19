package me.gilberto.essentials.management

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.EssentialsMain.pluginName
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

    fun convertmilisstring(to: Long, short: Boolean): String {
        val tosend = ArrayList<String>()
        fun helper(sendshort: String, send: String) {
            if (short) {
                tosend.add(sendshort)
            } else {
                tosend.add(send)
            }
        }
        val seconds = to / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val unidays = if (days < 1L) {
            Time.timeday
        } else Time.timedays
        helper("$days $timedayshort", "$days $unidays")
        val unihours = if (hours < 1L) {
            Time.timehour
        } else Time.timehours
        helper("${hours % 24} $timehourshort", "${hours % 24} $unihours")
        val uniminutes = if (minutes < 1L) {
            Time.timeminute
        } else Time.timeminutes
        helper("${minutes % 60} $timeminuteshort", "${minutes % 60} $uniminutes")
        val uniseconds = if (seconds < 1L) {
            Time.timesecond
        } else Time.timeseconds
        helper("${seconds % 60} $timesecondshort", "${seconds % 60} $uniseconds")
        var toreturn = ""
        var vezes = 0
        for (i in tosend) {
            vezes += 1
            toreturn = if (vezes == i.length) {
                if (toreturn == "") {
                    "$i."
                } else {
                    "$toreturn, i."
                }
            } else {
                if (toreturn == "") {
                    "$i."
                } else {
                    "$toreturn, $i"
                }
            }
        }
        return toreturn
    }

}