package github.gilbertokpl.core.internal.utils

import github.gilbertokpl.core.external.CorePlugin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

internal class InternalTime(lf: CorePlugin) {

    private val lunarFrame = lf

    private val MILLIS_TO_SECONDS = 1_000

    private val SECONDS_TO_MINUTES = 60

    private val HOURS_TO_DAYS = 24

    fun getOnlineTime(): Long {
        return System.currentTimeMillis() - lunarFrame.online
    }

    fun getCurrentDate(): String {
        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val now = LocalDateTime.now()
        return dtf.format(now)
    }

    fun convertStringToMillis(timeString: String): Long {
        val messageSplit = timeString.split(" ")
        var convert = 0L
        for (i in messageSplit) {
            val split = i.replace("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$".toRegex(), "1")
                .split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex())
            val unit = try {
                split[1]
            } catch (e: Throwable) {
                null
            }
            convert += if (unit == null) {
                try {
                    TimeUnit.MINUTES.toMillis(split[0].toLong())
                } catch (e: Throwable) {
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
                } catch (e: Throwable) {
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

        var seconds = time / MILLIS_TO_SECONDS
        var minutes = seconds / SECONDS_TO_MINUTES
        var hours = minutes / SECONDS_TO_MINUTES
        val days = hours / HOURS_TO_DAYS
        seconds %= SECONDS_TO_MINUTES
        minutes %= SECONDS_TO_MINUTES
        hours %= HOURS_TO_DAYS
        val uniDays = if (days < 2) {
            lunarFrame.getConfig().messages().timeDay
        } else lunarFrame.getConfig().messages().timeDays
        helper(days, "$days ${lunarFrame.getConfig().messages().timeDayShort}", "$days $uniDays")
        val uniHours = if (hours < 2) {
            lunarFrame.getConfig().messages().timeHour
        } else lunarFrame.getConfig().messages().timeHours
        helper(hours, "$hours ${lunarFrame.getConfig().messages().timeHourShort}", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            lunarFrame.getConfig().messages().timeMinute
        } else lunarFrame.getConfig().messages().timeMinutes
        helper(minutes, "$minutes ${lunarFrame.getConfig().messages().timeMinuteShort}", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            lunarFrame.getConfig().messages().timeSecond
        } else lunarFrame.getConfig().messages().timeSeconds
        helper(seconds, "$seconds ${lunarFrame.getConfig().messages().timeSecondShort}", "${seconds % 60} $uniSeconds")
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