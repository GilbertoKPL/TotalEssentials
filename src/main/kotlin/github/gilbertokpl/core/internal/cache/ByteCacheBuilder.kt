package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal class ByteCacheBuilder<T>(
    private val table: Table,
    private val primaryColumn: Column<String>,
    private val column: Column<T>
) : CacheBuilder<T> {

    private val hashMap = mutableMapOf<String, T?>()
    private val toUpdate = mutableListOf<String>()

    override fun getMap(): Map<String, T?> {
        return hashMap.toMap()
    }

    override operator fun get(entity: String): T? {
        return hashMap[entity.lowercase()]
    }

    override operator fun get(entity: Player): T? {
        return hashMap[entity.name.lowercase()]
    }

    override fun set(entity: String, value: T) {
        hashMap[entity.lowercase()] = value
        toUpdate.add(entity.lowercase())
    }

    override fun set(entity: String, value: T, override: Boolean) {
        set(entity, value)
    }

    override operator fun set(entity: Player, value: T) {
        set(entity.name, value)
    }

    override fun remove(entity: Player) {
        remove(entity.name)
    }

    override fun remove(entity: String) {
        hashMap[entity.lowercase()] = null
        toUpdate.add(entity.lowercase())
    }

    private fun save(list: List<String>) {
        if (toUpdate.isEmpty()) return
        val existingRows = table.select { primaryColumn inList toUpdate }.toList().associateBy { it[primaryColumn] }

        for (i in list) {
            if (i in toUpdate) {
                toUpdate.remove(i)
                val value = hashMap[i]

                if (value == null) {
                    existingRows[i]?.let { row ->
                        table.deleteWhere { primaryColumn eq row[primaryColumn] }
                    }
                } else {
                    if (existingRows[i] == null) {
                        table.insert {
                            it[primaryColumn] = i
                            it[column] = value
                        }
                    } else {
                        table.update({ primaryColumn eq i }) {
                            it[column] = value
                        }
                    }
                }
            }
        }
    }

    override fun update() {
        save(toUpdate.toList())
    }

    override fun load() {
        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = i[column]
        }
    }

    override fun unload() {
        save(toUpdate.toList())
    }
}
