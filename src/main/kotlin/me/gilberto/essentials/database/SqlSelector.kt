package me.gilberto.essentials.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.gilberto.essentials.config.configs.Database.mysqldatabase
import me.gilberto.essentials.config.configs.Database.mysqlip
import me.gilberto.essentials.config.configs.Database.mysqlpass
import me.gilberto.essentials.config.configs.Database.mysqlport
import me.gilberto.essentials.config.configs.Database.mysqlusername
import me.gilberto.essentials.config.configs.Database.sqlselector
import me.gilberto.essentials.config.configs.langs.Start.conectdatabase
import me.gilberto.essentials.config.configs.langs.Start.databaseerror
import me.gilberto.essentials.config.configs.langs.Start.databasesucesso
import me.gilberto.essentials.config.configs.langs.Start.databasevalid
import me.gilberto.essentials.config.configs.langs.Start.databasevalid1
import me.gilberto.essentials.database.SqlStart.start
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.jetbrains.exposed.sql.Database

class SqlSelector {
    init {
        consoleMessage(conectdatabase)
        try {
            when (sqlselector.lowercase()) {
                "sqlite" -> {
                    SqlInstance.SQL =
                        Database.connect("jdbc:sqlite:${pluginpastedir()}/GD_Essentials.db", "org.sqlite.JDBC")
                    start()
                }
                "h2" -> {
                    SqlInstance.SQL = Database.connect("jdbc:h2:./${pluginpastedir()}/GD_Essentials", "org.h2.Driver")
                    start()
                }
                "mysql" -> {
                    val config = HikariConfig().apply {
                        jdbcUrl = "jdbc:mysql://$mysqlip:$mysqlport/$mysqldatabase"
                        driverClassName = "com.mysql.cj.jdbc.Driver"
                        username = mysqlusername
                        password = mysqlpass
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    SqlInstance.SQL = Database.connect(dataSource)
                    start()
                }
                else -> {
                    consoleMessage(databasevalid)
                    consoleMessage(databasevalid1)
                }
            }
            consoleMessage(databasesucesso.replace("%db", sqlselector.lowercase()))
        } catch (ex: Exception) {
            consoleMessage(databaseerror)
        }
    }
}