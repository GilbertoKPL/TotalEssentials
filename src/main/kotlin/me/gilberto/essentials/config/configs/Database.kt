package me.gilberto.essentials.config.configs

import me.gilberto.essentials.config.ConfigMain

object Database {
    lateinit var sqlselector: String
    lateinit var mysqlip: String
    lateinit var mysqlport: String
    lateinit var mysqlusername: String
    lateinit var mysqldatabase: String
    lateinit var mysqlpass: String

    fun reload() {
        val source1 = ConfigMain.econf
        sqlselector = ConfigMain.getString(source1, "Database.type")
        mysqlip = ConfigMain.getString(source1, "Database.mysql.ip")
        mysqlport = ConfigMain.getString(source1, "Database.mysql.port")
        mysqlusername = ConfigMain.getString(source1, "Database.mysql.username")
        mysqldatabase = ConfigMain.getString(source1, "Database.mysql.database")
        mysqlpass = ConfigMain.getString(source1, "Database.mysql.pass")
    }
}