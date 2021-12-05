package github.gilbertokpl.essentialsk.tables

import org.jetbrains.exposed.sql.Table

object PlayerDataSQL : Table() {
    val uuid = varchar("uuid", 64)
    val kitsTime = text("kitsTime")
    val FakeNick = text("fakeNick")
    override val primaryKey = PrimaryKey(uuid)
}
