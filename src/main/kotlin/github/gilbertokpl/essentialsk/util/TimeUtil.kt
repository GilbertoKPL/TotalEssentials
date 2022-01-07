package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.IInstance
import java.util.concurrent.TimeUnit

class TimeUtil {

    fun convertStringToMillis(timeString: String): Long {
        val messageSplit = timeString.split(" ")
        var convert = 0L
        for (i in messageSplit) {
            val split = i.replace("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$".toRegex(), "1")
                .split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex())
            val unit = try {
                split[1]
            } catch (e: Exception) {
                null
            }
            convert += if (unit == null) {
                try {
                    TimeUnit.MINUTES.toMillis(split[0].toLong())
                } catch (e: Exception) {
                    0L
                }
            } else {
                try {
                    when (unit.lowercase()) {
                        "s" -> TimeUnit.SECONDS.toMillis(split[0].toLong())
                        "m" -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                        "h" -> TimeUnit.HOURS.toMillis(split[0].toLong())
                        "d" -> TimeUnit.DAYS.toMillis(split[0].toLong())
                        else -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                    }
                } catch (e: Exception) {
                    0L
                }
            }
        }
        return convert
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
            GeneralLang.getInstance().timeDay
        } else GeneralLang.getInstance().timeDays
        helper(days, "$days ${GeneralLang.getInstance().timeDayShort}", "$days $uniDays")
        val uniHours = if (hours < 2) {
            GeneralLang.getInstance().timeHour
        } else GeneralLang.getInstance().timeHours
        helper(hours, "$hours ${GeneralLang.getInstance().timeHourShort}", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            GeneralLang.getInstance().timeMinute
        } else GeneralLang.getInstance().timeMinutes
        helper(minutes, "$minutes ${GeneralLang.getInstance().timeMinuteShort}", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            GeneralLang.getInstance().timeSecond
        } else GeneralLang.getInstance().timeSeconds
        helper(seconds, "$seconds ${GeneralLang.getInstance().timeSecondShort}", "${seconds % 60} $uniSeconds")
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

    companion object : IInstance<TimeUtil> {
        private val instance = createInstance()
        override fun createInstance(): TimeUtil = TimeUtil()
        override fun getInstance(): TimeUtil {
            return instance
        }
    }
}
