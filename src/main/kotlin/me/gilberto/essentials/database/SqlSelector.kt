package me.gilberto.essentials.database

import me.gilberto.essentials.config.Config.mysqldatabase
import me.gilberto.essentials.config.Config.mysqlip
import me.gilberto.essentials.config.Config.mysqlpass
import me.gilberto.essentials.config.Config.mysqlport
import me.gilberto.essentials.config.Config.mysqlusername
import me.gilberto.essentials.config.Config.sqlselector
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.jetbrains.exposed.sql.Database

class SqlSelector {
    init {
        consoleMessage("${pluginpastedir().replace("\\", "/")}/GD_Essentials.db")
        when (sqlselector) {
            "sqlite" -> {
                SqlInstance.SQL = Database.connect("jdbc:sqlite:${pluginpastedir().replace("\\", "/")}/GD_Essentials.db", "org.sqlite.JDBC")
            }
            "h2" -> {
                Database.connect("jdbc:h2:./${pluginpastedir()}/GD_Essentials", "org.h2.Driver")
            }
            "mysql" -> {
                SqlInstance.SQL = Database.connect(
                    "jdbc:mysql://$mysqlip:$mysqlport/$mysqldatabase", driver = "com.mysql.jdbc.Driver",
                    user = mysqlusername, password = mysqlpass
                )
            }
            else -> {
                consoleMessage("Please select a valid Database")
                consoleMessage("Validates -> sqlite, h2, mysql")
            }
        }
    }
}