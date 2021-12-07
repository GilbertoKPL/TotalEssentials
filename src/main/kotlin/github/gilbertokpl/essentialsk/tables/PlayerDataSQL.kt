package github.gilbertokpl.essentialsk.tables

import org.jetbrains.exposed.sql.Table

object PlayerDataSQL : Table() {
    val uuid = varchar("uuid", 64)
    val kitsTime = text("kitsTime").default("")
    val savedHomes = text("savedHomes").default("")
    val FakeNick = varchar("fakeNick", 32).default("")
    override val primaryKey = PrimaryKey(uuid)
}
