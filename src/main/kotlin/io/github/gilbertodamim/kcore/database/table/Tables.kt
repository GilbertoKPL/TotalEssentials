package io.github.gilbertodamim.kcore.database.table

import org.jetbrains.exposed.sql.Table

object SqlKits : Table() {
    val kitName = varchar("kitName", 16)
    val kitRealName = varchar("kitRealName", 32)
    val kitTime = long("kitTime")
    val kitItems = text("kitItems")
    override val primaryKey = PrimaryKey(kitName)
}

object PlayerKits : Table() {
    val uuid = varchar("uuid", 64)
    val kitsTime = text("kitsTime")
    override val primaryKey = PrimaryKey(uuid)
}