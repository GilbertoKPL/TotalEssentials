package github.gilbertokpl.essentialsk.util

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.data.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.data.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.data.tables.WarpsDataSQL
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object SqlUtil {

    lateinit var sql: Database

    fun <T> helperUpdater(field: String, col: Column<T>, value: T) {
        TaskUtil.asyncExecutor {
            transaction(sql) {
                PlayerDataSQL.update({ PlayerDataSQL.playerTable eq field }) {
                    it[col] = value
                }
            }
        }
    }

    fun startSql() {
        MainUtil.consoleMessage(StartLang.connectDatabase)
        try {
            val type = MainConfig.databaseType
            when (type.lowercase()) {
                "h2" -> {
                    sql = Database.connect("jdbc:h2:./${MainUtil.mainPath}/sql/H2SQLV2", "org.h2.Driver")
                }
                "mysql" -> {
                    val config = HikariConfig().apply {
                        jdbcUrl =
                            "jdbc:mysql://${MainConfig.databaseSqlIp}" +
                                    ":${MainConfig.databaseSqlPort}" +
                                    "/${MainConfig.databaseSqlDatabase}"
                        driverClassName = "org.mariadb.jdbc.Driver"
                        username = MainConfig.databaseSqlUsername
                        password = MainConfig.databaseSqlPassword
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    sql = Database.connect(dataSource)
                }
                else -> {
                    sql = Database.connect("jdbc:h2:./${MainUtil.mainPath}/sql/H2SQLV2", "org.h2.Driver")
                    MainUtil.consoleMessage(StartLang.databaseValid)
                }
            }
            MainUtil
                .consoleMessage(StartLang.connectDatabaseSuccess.replace("%db%", type.lowercase()))
        } catch (ex: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
        }
    }

    fun startTables() {
        transaction(sql) {
            SchemaUtils.create(KitsDataSQL, PlayerDataSQL, WarpsDataSQL, SpawnDataSQL)
            SchemaUtils.createMissingTablesAndColumns(KitsDataSQL, PlayerDataSQL, WarpsDataSQL, SpawnDataSQL)
        }
    }
}
