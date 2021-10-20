package io.github.gilbertodamim.essentials.database.table

import org.jetbrains.exposed.sql.Table

object SqlKits : Table() {
    val kitName = varchar("kitName", 16)
    val kitRealName = varchar("kitRealName", 32)
    val kitTime = long("kitTime")
    val kitItems = varchar("kitItems", 255)
    override val primaryKey = PrimaryKey(kitName)
}

object PlayerKits : Table() {
    val uuid = varchar("uuid", 16)
    override val primaryKey = PrimaryKey(uuid)
}
