package github.gilbertokpl.core.external.cache.interfaces

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

interface CacheBase {

    var table: Table

    var primaryColumn: Column<String>


}