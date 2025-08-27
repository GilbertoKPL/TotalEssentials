package github.gilbertokpl.core.external.cache.interfaces

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table


interface CacheBase {

    var table: Table

    var primaryColumn: Column<String>

}