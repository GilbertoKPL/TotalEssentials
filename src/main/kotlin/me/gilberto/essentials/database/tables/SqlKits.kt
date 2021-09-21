package me.gilberto.essentials.database.tables

import org.jetbrains.exposed.sql.Table

object SqlKits : Table() {
    val kitnumber = integer("kitnumber").autoIncrement()
    val kitname = varchar("kit", 16)
    val kittime = integer("time")
    val kititens = varchar("kit", 600)
    override val primaryKey = PrimaryKey(kitnumber, kitname)
}