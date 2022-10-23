package github.gilbertokpl.total.database

import org.jetbrains.exposed.sql.Table

object SpawnDataSQL : Table() {
    val spawnNameTable = varchar("spawnName", 16)
    val spawnLocationTable = text("spawnLocation").default("")
    override val primaryKey = PrimaryKey(spawnNameTable)
}
