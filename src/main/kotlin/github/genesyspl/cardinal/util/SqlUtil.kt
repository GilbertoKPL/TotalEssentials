package github.genesyspl.cardinal.util

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import github.genesyspl.cardinal.configs.StartLang
import github.genesyspl.cardinal.manager.IInstance
import github.genesyspl.cardinal.tables.KitsDataSQL
import github.genesyspl.cardinal.tables.PlayerDataSQL
import github.genesyspl.cardinal.tables.SpawnDataSQL
import github.genesyspl.cardinal.tables.WarpsDataSQL
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SqlUtil {

    lateinit var sql: Database

    fun <T> helperUpdater(field: String, col: Column<T>, value: T) {
        TaskUtil.getInstance().asyncExecutor {
            transaction(sql) {
                PlayerDataSQL.update({ PlayerDataSQL.PlayerInfo eq field }) {
                    it[col] = value
                }
            }
        }
    }

    fun startSql() {
        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().connectDatabase)
        try {
            val type = github.genesyspl.cardinal.configs.MainConfig.getInstance().databaseType
            when (type.lowercase()) {
                "h2" -> {
                    sql =
                        Database.connect("jdbc:h2:./${PluginUtil.getInstance().mainPath}/sql/H2SQLV2", "org.h2.Driver")
                }
                "mysql" -> {
                    val config = HikariConfig().apply {
                        jdbcUrl =
                            "jdbc:mysql://${github.genesyspl.cardinal.configs.MainConfig.getInstance().databaseSqlIp}:${github.genesyspl.cardinal.configs.MainConfig.getInstance().databaseSqlPort}/${github.genesyspl.cardinal.configs.MainConfig.getInstance().databaseSqlDatabase}"
                        driverClassName = "org.mariadb.jdbc.Driver"
                        username = github.genesyspl.cardinal.configs.MainConfig.getInstance().databaseSqlUsername
                        password = github.genesyspl.cardinal.configs.MainConfig.getInstance().databaseSqlPassword
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    sql = Database.connect(dataSource)
                }
                else -> {
                    sql =
                        Database.connect("jdbc:h2:./${PluginUtil.getInstance().mainPath}/sql/H2SQLV2", "org.h2.Driver")
                    PluginUtil.getInstance().consoleMessage(StartLang.getInstance().databaseValid)
                }
            }
            PluginUtil.getInstance()
                .consoleMessage(StartLang.getInstance().connectDatabaseSuccess.replace("%db%", type.lowercase()))
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
        }
    }

    fun startTables() {
        transaction(sql) {
            SchemaUtils.create(KitsDataSQL, PlayerDataSQL, WarpsDataSQL, SpawnDataSQL)
            SchemaUtils.createMissingTablesAndColumns(KitsDataSQL, PlayerDataSQL, WarpsDataSQL, SpawnDataSQL)
        }
    }

    companion object : IInstance<SqlUtil> {
        private val instance = createInstance()
        override fun createInstance(): SqlUtil = SqlUtil()
        override fun getInstance(): SqlUtil {
            return instance
        }
    }

}
