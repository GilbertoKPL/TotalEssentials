package io.github.gilbertodamim.kcore.config.langs

import io.github.gilbertodamim.kcore.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object TimeLang {
    lateinit var timeSeconds: String
    lateinit var timeSecond: String
    lateinit var timeMinutes: String
    lateinit var timeMinute: String
    lateinit var timeHours: String
    lateinit var timeHour: String
    lateinit var timeDays: String
    lateinit var timeDay: String
    lateinit var timeSecondShort: String
    lateinit var timeMinuteShort: String
    lateinit var timeHourShort: String
    lateinit var timeDayShort: String

    fun reload(source1: YamlConfiguration) {
        timeSeconds = ConfigMain.getString(source1, "Time.time-seconds", false)
        timeSecond = ConfigMain.getString(source1, "Time.time-second", false)
        timeSecondShort = ConfigMain.getString(source1, "Time.time-second-short", false)
        timeMinutes = ConfigMain.getString(source1, "Time.time-minutes", false)
        timeMinute = ConfigMain.getString(source1, "Time.time-minute", false)
        timeMinuteShort = ConfigMain.getString(source1, "Time.time-minute-short", false)
        timeHours = ConfigMain.getString(source1, "Time.time-hours", false)
        timeHour = ConfigMain.getString(source1, "Time.time-hour", false)
        timeHourShort = ConfigMain.getString(source1, "Time.time-hour-short", false)
        timeDays = ConfigMain.getString(source1, "Time.time-days", false)
        timeDay = ConfigMain.getString(source1, "Time.time-day", false)
        timeDayShort = ConfigMain.getString(source1, "Time.time-day-short", false)
    }
}