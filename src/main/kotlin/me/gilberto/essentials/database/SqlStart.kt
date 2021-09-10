package me.gilberto.essentials.database

import me.gilberto.essentials.database.SqlInstance.SQL
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object SqlStart {
    fun startsqlinternal() {
        transaction(SQL) {
            SchemaUtils.create(SqlInternal)
        }
    }
}
