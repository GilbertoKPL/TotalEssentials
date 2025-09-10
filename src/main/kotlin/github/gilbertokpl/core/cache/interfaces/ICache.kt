package github.gilbertokpl.core.cache.interfaces

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table


interface ICache {

    var table: Table

    var primaryColumn: Column<String>

}