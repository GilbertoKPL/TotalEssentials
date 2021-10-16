package me.gilberto.essentials.database

import org.jetbrains.exposed.sql.Table

object SqlKits : Table() {
    val kitname = varchar("kitname", 16)
    val kitrealname = varchar("kitrealname", 32)
    val kittime = long("kittime").default(0)
    val kititens = varchar("kititens", 255)
    override val primaryKey = PrimaryKey(kitname)
}
object PlayerKits : Table() {
    val player = varchar("uuid", 16)
    override val primaryKey = PrimaryKey(player)
}
