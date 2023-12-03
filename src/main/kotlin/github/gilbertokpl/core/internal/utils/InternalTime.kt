package github.gilbertokpl.core.internal.utils

import github.gilbertokpl.core.external.CorePlugin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

internal class InternalTime(private val corePlugin: CorePlugin) {
    private val MILLIS_TO_SECONDS = 1_000L
    private val SECONDS_TO_MINUTES = 60L
    private val MINUTES_TO_HOURS = 60L
    private val HOURS_TO_DAYS = 24L

    fun getOnlineTime(): Long {
        return System.currentTimeMillis() - corePlugin.online
    }

    fun getCurrentDate(): String {
        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val now = LocalDateTime.now()
        return dtf.format(now)
    }

    fun convertStringToMillis(timeString: String): Long {
        val timeUnits = timeString.split(" ")
        var totalMillis = 0L

        for (unit in timeUnits) {
            val value = unit.substring(0, unit.length - 1).toLongOrNull() ?: continue
            val timeUnit = when (unit.last().lowercaseChar()) {
                's' -> TimeUnit.SECONDS
                'm' -> TimeUnit.MINUTES
                'h' -> TimeUnit.HOURS
                'd' -> TimeUnit.DAYS
                else -> TimeUnit.MINUTES
            }
            totalMillis += timeUnit.toMillis(value)
        }

        return totalMillis
    }

    fun convertMillisToString(time: Long, shortFormat: Boolean): String {
        val units = ArrayList<String>()

        var seconds = time / MILLIS_TO_SECONDS
        var minutes = seconds / SECONDS_TO_MINUTES
        var hours = minutes / MINUTES_TO_HOURS
        val days = hours / HOURS_TO_DAYS

        seconds %= SECONDS_TO_MINUTES
        minutes %= MINUTES_TO_HOURS
        hours %= HOURS_TO_DAYS

        if (days > 0) {
            val unit = if (shortFormat) corePlugin.getConfig().messages().timeDayShort else corePlugin.getConfig()
                .messages().timeDays
            units.add("$days $unit")
        }

        if (hours > 0) {
            val unit = if (shortFormat) corePlugin.getConfig().messages().timeHourShort else corePlugin.getConfig()
                .messages().timeHours
            units.add("$hours $unit")
        }

        if (minutes > 0) {
            val unit = if (shortFormat) corePlugin.getConfig().messages().timeMinuteShort else corePlugin.getConfig()
                .messages().timeMinutes
            units.add("$minutes $unit")
        }

        if (seconds > 0) {
            val unit = if (shortFormat) corePlugin.getConfig().messages().timeSecondShort else corePlugin.getConfig()
                .messages().timeSeconds
            units.add("$seconds $unit")
        }

        val delimiter = if (shortFormat) ", " else ", "
        var result = units.joinToString(delimiter)

        if (result.isNotEmpty() && !shortFormat) {
            result += "."
        }

        return result
    }
}
