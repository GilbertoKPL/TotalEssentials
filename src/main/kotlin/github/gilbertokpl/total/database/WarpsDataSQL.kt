package github.gilbertokpl.total.database

import org.jetbrains.exposed.sql.Table

object WarpsDataSQL : Table() {
    val warpNameTable = varchar("warpName", 16)
    val warpLocationTable = text("warpLocation").default("")
    override val primaryKey = PrimaryKey(warpNameTable)
}
