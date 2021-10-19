package me.gilberto.essentials.database

import org.jetbrains.exposed.sql.Table

object SqlKits : Table() {
    val kitname = varchar("kitname", 16)
    val kitrealname = varchar("kitrealname", 32)
    val kittime = long("kittime")
    val kititens = varchar("kititens", 255)
    override val primaryKey = PrimaryKey(kitname)
}
object PlayerKits : Table() {
    val uuid = varchar("uuid", 16)
    override val primaryKey = PrimaryKey(uuid)
}
