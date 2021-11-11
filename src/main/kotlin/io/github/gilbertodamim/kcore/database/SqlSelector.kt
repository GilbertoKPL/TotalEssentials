package io.github.gilbertodamim.kcore.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.gilbertodamim.kcore.config.configs.DatabaseConfig.*
import io.github.gilbertodamim.kcore.config.langs.StartLang.*
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.management.Manager.consoleMessage
import io.github.gilbertodamim.kcore.management.Manager.pluginPasteDir
import org.jetbrains.exposed.sql.Database

class SqlSelector {
    init {
        consoleMessage(connectDatabase)
        try {
            when (type.lowercase()) {
                "h2" -> {
                    SqlInstance.SQL = Database.connect("jdbc:h2:./${pluginPasteDir()}/KCoreH2SQL", "org.h2.Driver")
                }
                "mysql" -> {
                    val config = HikariConfig().apply {
                        jdbcUrl = "jdbc:mysql://$sqlIp:$sqlPort/$sqlDatabase"
                        driverClassName = "org.mariadb.jdbc.Driver"
                        username = sqlUsername
                        password = sqlPassword
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    SqlInstance.SQL = Database.connect(dataSource)
                }
                else -> {
                    SqlInstance.SQL = Database.connect("jdbc:h2:./${pluginPasteDir()}/KCoreH2SQL", "org.h2.Driver")
                    consoleMessage(databaseValid)
                }
            }
            consoleMessage(connectDatabaseSuccess.replace("%db%", type.lowercase()))
        } catch (ex: Exception) {
            consoleMessage(connectDatabaseError)
            ErrorClass.sendException(ex)
        }
    }
}