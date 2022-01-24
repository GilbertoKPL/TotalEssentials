package github.gilbertokpl.essentialsk.data.tables

import org.jetbrains.exposed.sql.Table

internal object SpawnDataSQL : Table() {
    val spawnName = varchar("spawnName", 16)
    val spawnLocation = text("spawnLocation").default("")
    override val primaryKey = PrimaryKey(spawnName)
}
