package me.gilberto.essentials.database

import me.gilberto.essentials.database.SqlInstance.SQL
import me.gilberto.essentials.database.tables.SqlKits
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object SqlStart {
    fun start() {
        transaction(SQL) {
            SchemaUtils.create(SqlKits)
        }
    }
}
