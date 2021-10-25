package io.github.gilbertodamim.ksystem.management

import io.github.gilbertodamim.ksystem.KSystemMain.instance
import io.github.gilbertodamim.ksystem.KSystemMain.pluginName
import io.github.gilbertodamim.ksystem.config.langs.TimeLang
import io.github.gilbertodamim.ksystem.config.langs.TimeLang.timeDayShort
import io.github.gilbertodamim.ksystem.config.langs.TimeLang.timeHourShort
import io.github.gilbertodamim.ksystem.config.langs.TimeLang.timeMinuteShort
import io.github.gilbertodamim.ksystem.config.langs.TimeLang.timeSecondShort
import org.bukkit.entity.Player

object Manager {
    fun consoleMessage(msg: String) {
        instance.server.consoleSender.sendMessage("$pluginName $msg")
    }
    fun pluginPasteDir(): String = instance.dataFolder.path
    fun pluginLangDir(): String = instance.dataFolder.path + "/lang/"
    fun getPlayerUUID(p : Player) : String{
        return p.uniqueId.toString()
    }
    fun convertMillisToString(time: Long, short: Boolean): String {
        val toSend = ArrayList<String>()
        fun helper(time: Long, sendShort: String, send: String) {
            if (time > 0L) {
                if (short) {
                    toSend.add(sendShort)
                } else {
                    toSend.add(send)
                }
            }
        }
        var seconds = time / 1000
        var minutes = seconds / 60
        var hours = minutes / 60
        val days = hours / 24
        seconds %= 60
        minutes %= 60
        hours %= 24
        val uniDays = if (days < 2) {
            TimeLang.timeDay
        } else TimeLang.timeDays
        helper(days, "$days $timeDayShort", "$days $uniDays")
        val uniHours = if (hours < 2) {
            TimeLang.timeHour
        } else TimeLang.timeHours
        helper(hours, "$hours $timeHourShort", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            TimeLang.timeMinute
        } else TimeLang.timeMinutes
        helper(minutes, "$minutes $timeMinuteShort", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            TimeLang.timeSecond
        } else TimeLang.timeSeconds
        helper(seconds, "$seconds $timeSecondShort", "${seconds % 60} $uniSeconds")
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
                    values
                } else {
                    "$toReturn, $values"
                }
            }
        }
        return toReturn
    }
}