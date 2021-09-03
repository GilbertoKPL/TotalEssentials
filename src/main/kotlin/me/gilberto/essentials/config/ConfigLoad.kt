package me.gilberto.essentials.config

class ConfigLoad {
    init {
        val source1 = ConfigMain.econf
        Config.sqlselector = ConfigMain.getString(source1, "Database.type")
        Config.mysqlip = ConfigMain.getString(source1, "Database.mysql.ip")
        Config.mysqlport = ConfigMain.getString(source1, "Database.mysql.port")
        Config.mysqlusername = ConfigMain.getString(source1, "Database.mysql.username")
        Config.mysqldatabase = ConfigMain.getString(source1, "Database.mysql.database")
        Config.mysqlpass = ConfigMain.getString(source1, "Database.mysql.pass")
    }
}