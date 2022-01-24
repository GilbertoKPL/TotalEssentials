package github.gilbertokpl.essentialsk.data.tables

import org.jetbrains.exposed.sql.Table

internal object KitsDataSQL : Table() {
    val kitName = varchar("kitName", 16)
    val kitFakeName = varchar("kitRealName", 32).default("")
    val kitTime = long("kitTime").default(0L)
    val kitItems = text("kitItems").default("")
    val kitWeight = integer("kitWeight").default(0)
    override val primaryKey = PrimaryKey(kitName)
}
