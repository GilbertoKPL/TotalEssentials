package github.gilbertokpl.essentialsk.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.data.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.data.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.data.tables.WarpsDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import net.dv8tion.jda.api.entities.TextChannel
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object DataManager {

    lateinit var sql: Database

    //editKitInv
    val editKitInventory = HashMap<Int, ItemStack>(50)

    //kitInv
    val kitGuiCache = HashMap<Int, Inventory>(10)

    //click kitInv
    val kitClickGuiCache = HashMap<Int, String>(40)

    //editKit
    val editKit = HashMap<Player, String>(10)

    val editKitChat = HashMap<Player, String>(10)

    val hashTextChannel = HashMap<String, TextChannel>()

    private val updateList = HashMap<Save, Sql>()

    internal enum class TrasactionType {
        SET,
        DELETE
    }

    internal data class Save(
        val entity : String,
        val table: Table
    )

    internal data class Sql(
        val type: TrasactionType,
        val set: HashMap<Column<*>, Any>?
    )

    private fun checkColumn(table: Table): Column<String> {
        return when (table) {
            PlayerDataSQL -> PlayerDataSQL.playerTable
            KitsDataSQL -> KitsDataSQL.kitName
            SpawnDataSQL -> SpawnDataSQL.spawnName
            WarpsDataSQL -> WarpsDataSQL.warpName
            else -> PlayerDataSQL.playerTable
        }
    }

    fun Table.del(entity: String) {
        updateList[Save(entity, Table(tableName))] =
            Sql(
                TrasactionType.DELETE,
                null
            )
    }

    fun Table.put(entity: String, set: HashMap<Column<*>, Any>) {
        val table = Table(tableName)
        val update = updateList[Save(entity, table)]

        if (update == null || update.type == TrasactionType.DELETE) {
            updateList[Save(entity, table)] = Sql(
                TrasactionType.SET,
                set
            )
            return
        }

        for (i in set) {
            update.set!![i.key] = i.value
        }
    }

    fun save() {
        transaction(sql) {
            for (i in updateList) {
                val check = i.key.table.select{ checkColumn(i.key.table) eq i.key.entity }
                when (i.value.type) {
                    TrasactionType.SET -> {
                        if (check.empty()) {
                            i.key.table.insert {
                                it[checkColumn(i.key.table)] = i.key.entity
                            }
                        }

                        if (i.value.set!!.isEmpty()) continue

                        i.key.table.update({ checkColumn(i.key.table) eq i.key.entity }) {
                            for (set in i.value.set!!) {
                                when (set.value) {
                                    is String -> {
                                        it[set.key as Column<String>] = set.value as String
                                    }
                                    is Int -> {
                                        it[set.key as Column<Int>] = set.value as Int
                                    }
                                    is Long -> {
                                        it[set.key as Column<Long>] = set.value as Long
                                    }
                                }
                            }
                        }
                    }
                    TrasactionType.DELETE -> {
                        if (!check.empty()) {
                            i.key.table.deleteWhere { checkColumn(i.key.table) eq i.key.entity }
                        }
                    }
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
            MainUtil.consoleMessage(StartLang.connectDatabaseSuccess.replace("%db%", type.lowercase()))
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
