package me.gilberto.essentials.database

import org.jetbrains.exposed.sql.Table

object SqlInternal : Table() {
    val player = varchar("player", 16)
    override val primaryKey = PrimaryKey(player)
}