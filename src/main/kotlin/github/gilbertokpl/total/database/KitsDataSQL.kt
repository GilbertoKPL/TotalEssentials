package github.gilbertokpl.total.database

import org.jetbrains.exposed.sql.Table

object KitsDataSQL : Table() {
    val kitNameTable = varchar("kitName", 16)
    val kitFakeNameTable = varchar("kitRealName", 32).default("")
    val kitTimeTable = long("kitTime").default(0L)
    val kitItemsTable = text("kitItems").default("")
    val kitWeightTable = integer("kitWeight").default(0)
    override val primaryKey = PrimaryKey(kitNameTable)
}
