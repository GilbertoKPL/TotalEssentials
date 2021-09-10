package me.gilberto.essentials.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.gilberto.essentials.config.Config.mysqldatabase
import me.gilberto.essentials.config.Config.mysqlip
import me.gilberto.essentials.config.Config.mysqlpass
import me.gilberto.essentials.config.Config.mysqlport
import me.gilberto.essentials.config.Config.mysqlusername
import me.gilberto.essentials.config.Config.sqlselector
import me.gilberto.essentials.database.SqlStart.startsqlinternal
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.jetbrains.exposed.sql.Database

class SqlSelector {
    init {
        when (sqlselector.lowercase()) {
            "sqlite" -> {
                SqlInstance.SQL = Database.connect("jdbc:sqlite:${pluginpastedir()}/GD_Essentials.db", "org.sqlite.JDBC")
                startsqlinternal()
            }
            "h2" -> {
                SqlInstance.SQL = Database.connect("jdbc:h2:./${pluginpastedir()}/GD_Essentials", "org.h2.Driver")
                startsqlinternal()
            }
            "mysql" -> {
                val config = HikariConfig().apply {
                    jdbcUrl         = "jdbc:mysql://$mysqlip:$mysqlport/$mysqldatabase"
                    driverClassName = "com.mysql.cj.jdbc.Driver"
                    username        = mysqlusername
                    password        = mysqlpass
                    maximumPoolSize = 10
                }
                val dataSource = HikariDataSource(config)
                SqlInstance.SQL = Database.connect(dataSource)
                startsqlinternal()
            }
            else -> {
                consoleMessage("Please select a valid Database")
                consoleMessage("Validates -> sqlite, h2, mysql")
            }
        }
    }
}