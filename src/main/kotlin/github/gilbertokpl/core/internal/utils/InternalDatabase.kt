package github.gilbertokpl.core.internal.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import github.gilbertokpl.core.external.CorePlugin
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLTransientConnectionException

internal class InternalDatabase(private val corePlugin: CorePlugin) {

    fun start(databaseTablePackage: List<Table>) {
        try {

            val database = when (corePlugin.getConfig().configs().databaseType.lowercase()) {
                "h2" -> {
                    val databasePath = "./${corePlugin.mainPath}/sql/H2database"
                    Database.connect("jdbc:h2:$databasePath", "org.h2.Driver")
                }

                "mysql" -> {
                    val config = HikariConfig().apply {
                        val databaseSqlIp = corePlugin.getConfig().configs().databaseSqlIp
                        val databaseSqlPort = corePlugin.getConfig().configs().databaseSqlPort
                        val databaseSqlDatabase = corePlugin.getConfig().configs().databaseSqlDatabase
                        val databaseSqlUsername = corePlugin.getConfig().configs().databaseSqlUsername
                        val databaseSqlPassword = corePlugin.getConfig().configs().databaseSqlPassword

                        jdbcUrl = "jdbc:mariadb://$databaseSqlIp:$databaseSqlPort/$databaseSqlDatabase"
                        driverClassName = "org.mariadb.jdbc.Driver"
                        username = databaseSqlUsername
                        password = databaseSqlPassword
                        maximumPoolSize = 40
                    }
                    val dataSource = HikariDataSource(config)
                    Database.connect(dataSource)
                }

                else -> {
                    val databasePath = "./${corePlugin.mainPath}/sql/H2database"
                    Database.connect("jdbc:h2:$databasePath", "org.h2.Driver")
                }
            }

            corePlugin.sql = database

            for (table in databaseTablePackage) {
                transaction(database) {
                    SchemaUtils.createMissingTablesAndColumns(table)
                }
            }

        } catch (e: Throwable) {
            println(e)
            Bukkit.getServer().shutdown()
        } catch (e: SQLTransientConnectionException) {
            println(e)
            Bukkit.getServer().shutdown()
        }
    }
}
