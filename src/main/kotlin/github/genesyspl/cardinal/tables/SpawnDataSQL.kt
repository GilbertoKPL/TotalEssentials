package github.genesyspl.cardinal.tables

import org.jetbrains.exposed.sql.Table

object SpawnDataSQL : Table() {
    val spawnName = varchar("spawnName", 16)
    val spawnLocation = text("spawnLocation").default("")
    override val primaryKey = PrimaryKey(spawnName)
}