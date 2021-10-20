package io.github.gilbertodamim.essentials.management

import io.github.gilbertodamim.essentials.EssentialsMain.instance
import io.github.gilbertodamim.essentials.EssentialsMain.pluginName
import io.github.gilbertodamim.essentials.config.langs.TimeLang
import io.github.gilbertodamim.essentials.config.langs.TimeLang.timeDayShort
import io.github.gilbertodamim.essentials.config.langs.TimeLang.timeHourShort
import io.github.gilbertodamim.essentials.config.langs.TimeLang.timeMinuteShort
import io.github.gilbertodamim.essentials.config.langs.TimeLang.timeSecondShort

object Manager {
    fun consoleMessage(msg: String) {
        instance.server.consoleSender.sendMessage("$pluginName $msg")
    }

    fun pluginPasteDir(): String = instance.dataFolder.path
    fun pluginLangDir(): String = instance.dataFolder.path + "/lang/"
    fun convertMillisToString(to: Long, short: Boolean): String {
        val toSend = ArrayList<String>()
        fun helper(sendShort: String, send: String) {
            if (short) {
                toSend.add(sendShort)
            } else {
                toSend.add(send)
            }
        }

        val seconds = to / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val uniDays = if (days < 1L) {
            TimeLang.timeDay
        } else TimeLang.timeDays
        helper("$days $timeDayShort", "$days $uniDays")
        val uniHours = if (hours < 1L) {
            TimeLang.timeHour
        } else TimeLang.timeHours
        helper("${hours % 24} $timeHourShort", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 1L) {
            TimeLang.timeMinute
        } else TimeLang.timeMinutes
        helper("${minutes % 60} $timeMinuteShort", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 1L) {
            TimeLang.timeSecond
        } else TimeLang.timeSeconds
        helper("${seconds % 60} $timeSecondShort", "${seconds % 60} $uniSeconds")
        var toReturn = ""
        var quaint = 0
        for (values in toSend) {
            quaint += 1
            toReturn = if (quaint == values.length) {
                if (toReturn == "") {
                    "$values."
                } else {
                    "$toReturn, $values."
                }
            } else {
                if (toReturn == "") {
                    "$values."
                } else {
                    "$toReturn, $values"
                }
            }
        }
        return toReturn
    }

}