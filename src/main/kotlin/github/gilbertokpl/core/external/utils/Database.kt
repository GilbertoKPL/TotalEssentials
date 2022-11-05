package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.utils.InternalDatabase
import org.jetbrains.exposed.sql.Table

class Database(lf: CorePlugin) {
    private val databaseInstance = InternalDatabase(lf)

    fun start(databaseTable: List<Table>) {
        databaseInstance.start(databaseTable)
    }
}