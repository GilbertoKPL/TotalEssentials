package github.gilbertokpl.essentialsk.data.tables

import org.jetbrains.exposed.sql.Table

object WarpsDataSQL : Table() {
    val warpName = varchar("warpName", 16)
    val warpLocation = text("warpLocation").default("")
    override val primaryKey = PrimaryKey(warpName)
}
