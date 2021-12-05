package github.gilbertokpl.essentialsk.tables

import org.jetbrains.exposed.sql.Table

object KitsDataSQL : Table() {
    val kitName = varchar("kitName", 16)
    val kitFakeName = varchar("kitRealName", 32)
    val kitTime = long("kitTime")
    val kitItems = text("kitItems")
    override val primaryKey = PrimaryKey(kitName)
}