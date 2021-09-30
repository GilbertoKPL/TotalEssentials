package me.gilberto.essentials.config.configs

import me.gilberto.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object Database {
    lateinit var sqlselector: String
    lateinit var mysqlip: String
    lateinit var mysqlport: String
    lateinit var mysqlusername: String
    lateinit var mysqldatabase: String
    lateinit var mysqlpass: String
    lateinit var langname: String

    fun reload(source1: YamlConfiguration) {
        langname = ConfigMain.getString(source1, "Lang",false)
        sqlselector = ConfigMain.getString(source1, "Database.type",false)
        mysqlip = ConfigMain.getString(source1, "Database.mysql.ip",false)
        mysqlport = ConfigMain.getString(source1, "Database.mysql.port",false)
        mysqlusername = ConfigMain.getString(source1, "Database.mysql.username",false)
        mysqldatabase = ConfigMain.getString(source1, "Database.mysql.database",false)
        mysqlpass = ConfigMain.getString(source1, "Database.mysql.pass",false)
    }
}