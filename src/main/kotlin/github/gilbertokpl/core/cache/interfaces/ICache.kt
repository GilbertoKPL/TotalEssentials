package github.gilbertokpl.core.cache.interfaces

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

/**
 * Interface que define a estrutura base de uma tabela de cache.
 */
interface ICache {
    val table: Table
    val primaryColumn: Column<String>
}
