package me.gilberto.essentials.database.tables

import org.jetbrains.exposed.sql.Table

object SqlKits : Table() {
    val kit = varchar("kit", 16)
    val time = integer("time")

}