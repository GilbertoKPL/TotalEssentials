package github.gilbertokpl.essentialsk.util

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class SqlUtil {

    lateinit var sql: Database

    fun startSql() {
        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().connectDatabase)
        try {
            val type = MainConfig.getInstance().databaseType
            when (type.lowercase()) {
                "h2" -> {
                    sql = Database.connect("jdbc:h2:./${PluginUtil.getInstance().mainPath}/sql/H2SQL", "org.h2.Driver")
                }
                "mysql" -> {
                    val config = HikariConfig().apply {
                        jdbcUrl =
                            "jdbc:mysql://${MainConfig.getInstance().databaseSqlIp}:${MainConfig.getInstance().databaseSqlPort}/${MainConfig.getInstance().databaseSqlDatabase}"
                        driverClassName = "org.mariadb.jdbc.Driver"
                        username = MainConfig.getInstance().databaseSqlUsername
                        password = MainConfig.getInstance().databaseSqlPassword
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    sql = Database.connect(dataSource)
                }
                else -> {
                    sql = Database.connect("jdbc:h2:./${PluginUtil.getInstance().mainPath}/sql/H2SQL", "org.h2.Driver")
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
            SchemaUtils.create(KitsDataSQL, PlayerDataSQL)
            SchemaUtils.createMissingTablesAndColumns(KitsDataSQL, PlayerDataSQL)
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
