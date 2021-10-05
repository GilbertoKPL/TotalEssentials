package me.gilberto.essentials.config.configs.langs

import me.gilberto.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object Time {
    lateinit var timeseconds: String
    lateinit var timesecond: String
    lateinit var timeminutes: String
    lateinit var timeminute: String
    lateinit var timehours: String
    lateinit var timehour: String
    lateinit var timedays: String
    lateinit var timeday: String
    lateinit var timesecondshort: String
    lateinit var timeminuteshort: String
    lateinit var timehourshort: String
    lateinit var timedayshort: String

    fun reload(source1: YamlConfiguration) {
        timeseconds = ConfigMain.getString(source1, "Time.timeseconds", false)
        timesecond = ConfigMain.getString(source1, "Time.timesecond", false)
        timesecondshort = ConfigMain.getString(source1, "Time.timesecond", false)
        timeminutes = ConfigMain.getString(source1, "Time.timeminutes", false)
        timeminute = ConfigMain.getString(source1, "Time.timeminute", false)
        timeminuteshort = ConfigMain.getString(source1, "Time.timeminute", false)
        timehours = ConfigMain.getString(source1, "Time.timehours", false)
        timehour = ConfigMain.getString(source1, "Time.timehour", false)
        timehourshort = ConfigMain.getString(source1, "Time.timehour", false)
        timedays = ConfigMain.getString(source1, "Time.timedays", false)
        timeday = ConfigMain.getString(source1, "Time.timeday", false)
        timedayshort = ConfigMain.getString(source1, "Time.timeday", false)
    }
}