package github.gilbertokpl.essentialsk.tables

import org.jetbrains.exposed.sql.Table

object PlayerDataSQL : Table() {
    val PlayerInfo = varchar("uuid", 64)
    val KitsTime = text("kitsTime").default("")
    val SavedHomes = text("savedHomes").default("")
    val FakeNick = varchar("fakeNick", 32).default("")
    val GameMode = integer("Gamemode").default(0)
    val Vanish = bool("Vanish").default(false)
    val Light = bool("Light").default(false)
    val Fly = bool("Fly").default(false)
    override val primaryKey = PrimaryKey(PlayerInfo)
}
