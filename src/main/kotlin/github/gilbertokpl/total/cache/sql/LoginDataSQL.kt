package github.gilbertokpl.total.cache.sql

import org.jetbrains.exposed.v1.core.Table


object LoginDataSQL : Table() {
    val player = varchar("player", 16)
    val ip = varchar("ip", 16).default("0.0.0.0")
    val password = varchar("password", 26).default("112233")
    override val primaryKey = PrimaryKey(player)
}