package github.gilbertokpl.total.cache.sql

import github.gilbertokpl.total.config.files.MainConfig
import org.jetbrains.exposed.sql.Table

object SpawnDataSQL : Table("SpawnData" + MainConfig.databaseManager) {
    val spawnNameTable = varchar("spawnName", 16)
    val spawnLocationTable = text("spawnLocation").default("")
    override val primaryKey = PrimaryKey(spawnNameTable)
}
