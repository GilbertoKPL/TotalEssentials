package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.GeneralLang
import java.util.concurrent.TimeUnit

object TimeUtil {

    private const val MILLIS_TO_SECOUNDS = 1_000

    private const val SECOUNDS_TO_MINUTES = 60

    private const val HOURS_TO_DAYS = 24

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

        var seconds = time / MILLIS_TO_SECOUNDS
        var minutes = seconds / SECOUNDS_TO_MINUTES
        var hours = minutes / SECOUNDS_TO_MINUTES
        val days = hours / HOURS_TO_DAYS
        seconds %= SECOUNDS_TO_MINUTES
        minutes %= SECOUNDS_TO_MINUTES
        hours %= HOURS_TO_DAYS
        val uniDays = if (days < 2) {
            GeneralLang.timeDay
        } else GeneralLang.timeDays
        helper(days, "$days ${GeneralLang.timeDayShort}", "$days $uniDays")
        val uniHours = if (hours < 2) {
            GeneralLang.timeHour
        } else GeneralLang.timeHours
        helper(hours, "$hours ${GeneralLang.timeHourShort}", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            GeneralLang.timeMinute
        } else GeneralLang.timeMinutes
        helper(minutes, "$minutes ${GeneralLang.timeMinuteShort}", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            GeneralLang.timeSecond
        } else GeneralLang.timeSeconds
        helper(seconds, "$seconds ${GeneralLang.timeSecondShort}", "${seconds % 60} $uniSeconds")
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
