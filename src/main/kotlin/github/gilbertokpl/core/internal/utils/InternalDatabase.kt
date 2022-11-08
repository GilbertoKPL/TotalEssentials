package github.gilbertokpl.core.internal.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import github.gilbertokpl.core.external.CorePlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

internal class InternalDatabase(lf: CorePlugin) {

    private val lunarFrame = lf
    fun start(databaseTablePackage: List<Table>) {

        try {
            val type = lunarFrame.getConfig().configs().databaseType
            when (type.lowercase()) {
                "h2" -> {
                    lunarFrame.sql = Database.connect("jdbc:h2:./${lunarFrame.mainPath}/sql/H2database", "org.h2.Driver")
                }

                "mysql" -> {
                    val config = HikariConfig().apply {
                        jdbcUrl =
                            "jdbc:mariadb://${lunarFrame.getConfig().configs().databaseSqlIp}" +
                                    ":${lunarFrame.getConfig().configs().databaseSqlPort}" +
                                    "/${lunarFrame.getConfig().configs().databaseSqlDatabase}"
                        driverClassName = "org.mariadb.jdbc.Driver"
                        username = lunarFrame.getConfig().configs().databaseSqlUsername
                        password = lunarFrame.getConfig().configs().databaseSqlPassword
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    lunarFrame.sql = Database.connect(dataSource)
                }

                else -> {
                    lunarFrame.sql = Database.connect("jdbc:h2:./${lunarFrame.mainPath}/sql/H2database", "org.h2.Driver")
                }
            }
        } catch (e: Throwable) {
            println(e)
        }

        for (cl in databaseTablePackage) {
            transaction(lunarFrame.sql) {
                SchemaUtils.createMissingTablesAndColumns(cl)
            }
        }

    }
}