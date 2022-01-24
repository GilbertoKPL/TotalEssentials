package github.gilbertokpl.essentialsk.data.tables

import org.jetbrains.exposed.sql.Table

object PlayerDataSQL : Table() {
    val playerTable = varchar("uuid", 64)
    val kitsTable = text("kitsTime").default("")
    val homeTable = text("savedHomes").default("")
    val nickTable = varchar("fakeNick", 32).default("")
    val gameModeTable = integer("Gamemode").default(0)
    val vanishTable = bool("Vanish").default(false)
    val lightTable = bool("Light").default(false)
    val flyTable = bool("Fly").default(false)
    val backTable = text("Back").default("")
    val speedTable = integer("Speed").default(1)
    override val primaryKey = PrimaryKey(playerTable)
}

