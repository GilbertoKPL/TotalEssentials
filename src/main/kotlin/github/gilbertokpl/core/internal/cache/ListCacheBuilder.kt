package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.convert.SerializerBase
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilderV2
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal class ListCacheBuilder<K, V>(
    private val table: Table,
    private val primaryColumn: Column<String>,
    private val column: Column<K>,
    private val classConvert: SerializerBase<ArrayList<V>, K>
) : CacheBuilderV2<ArrayList<V>, V> {


    private val hashMap = HashMap<String, ArrayList<V>?>()
    private val toUpdate = mutableListOf<String>()

    override fun getMap(): Map<String, ArrayList<V>?> {
        return hashMap.toMap()
    }

    override operator fun get(entity: String): ArrayList<V>? {
        return hashMap[entity.lowercase()]
    }

    override operator fun get(entity: Player): ArrayList<V>? {
        return hashMap[entity.name.lowercase()]
    }

    override operator fun set(entity: Player, value: ArrayList<V>) {
        set(entity.name, value)
    }

    override operator fun set(entity: String, value: ArrayList<V>, override: Boolean) {
        if (override) {
            hashMap[entity.lowercase()] = value
            toUpdate.add(entity.lowercase())
            return
        }
        set(entity, value)
        return
    }

    override operator fun set(entity: String, value: ArrayList<V>) {
        val ent = hashMap[entity.lowercase()] ?: ArrayList()
        ent.addAll(value)
        hashMap[entity.lowercase()] = ent
        toUpdate.add(entity.lowercase())
    }

    override fun remove(entity: Player, value: V) {
        remove(entity.name, value)
    }

    override fun remove(entity: String, value: V) {
        val ent = hashMap[entity.lowercase()] ?: return
        ent.remove(value)
        hashMap[entity.lowercase()] = ent
        toUpdate.add(entity.lowercase())
    }

    override fun remove(entity: Player) {
        remove(entity.name.lowercase())
    }

    override fun remove(entity: String) {
        hashMap[entity.lowercase()] = null
        toUpdate.add(entity.lowercase())
    }

    override fun update() {
        save(toUpdate.toList())
    }

    private fun save(list: List<String>) {

        if (toUpdate.isEmpty()) return

        val existingRows = table.select { primaryColumn inList toUpdate }.toList().associateBy { it[primaryColumn] }

        for (i in list) {
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
                        it[column] = classConvert.convertToDatabase(value)
                    }
                } else {
                    table.update({ primaryColumn eq i }) {
                        it[column] = classConvert.convertToDatabase(value)
                    }
                }
            }
        }
    }

    override fun load() {
        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = classConvert.convertToCache(i[column]) ?: ArrayList()
        }
    }

    override fun unload() {
        save(toUpdate.toList())
    }
}
