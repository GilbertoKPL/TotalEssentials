package github.gilbertokpl.base.external.utils

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.internal.utils.InternalDatabase
import org.jetbrains.exposed.sql.Table

class Database(lf: BasePlugin) {
    private val databaseInstance = InternalDatabase(lf)

    fun start(databaseTable: List<Table>) {
        databaseInstance.start(databaseTable)
    }
}