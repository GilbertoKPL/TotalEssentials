package github.gilbertokpl.total.cache.sql

import github.gilbertokpl.total.config.files.MainConfig
import org.jetbrains.exposed.v1.core.Table

object WarpsDataSQL : Table("WarpsData" + MainConfig.databaseManager) {
    val warpNameTable = varchar("warpName", 16)
    val warpLocationTable = text("warpLocation").default("")
    override val primaryKey = PrimaryKey(warpNameTable)
}
