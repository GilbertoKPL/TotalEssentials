package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.convert.SerializerBase
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilderV2
import okhttp3.internal.toImmutableList
import okhttp3.internal.toImmutableMap
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal class ListCacheBuilder<K, V>(
    t: Table,
    pc: Column<String>,
    c: Column<K>,
    classConverter: SerializerBase<ArrayList<V>, K>
) :
    CacheBuilderV2<ArrayList<V>, V> {

    private val conv = classConverter

    private val table = t

    private val column = c

    private val primaryColumn = pc

    private val hashMap = HashMap<String, ArrayList<V>?>()

    override fun getMap(): Map<String, ArrayList<V>?> {
        return hashMap.toImmutableMap()
    }

    private val toUpdate = ArrayList<String>()

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
        } else {
            set(entity, value)
        }
    }

    override operator fun set(entity: String, value: ArrayList<V>) {
        val ent = hashMap[entity.lowercase()] ?: let {
            hashMap[entity.lowercase()] = value
            toUpdate.add(entity.lowercase())
            return
        }
        ent.addAll(value)
        toUpdate.add(entity.lowercase())
    }

    override fun remove(entity: Player, value: V) {
        remove(entity.name, value)
    }

    override fun remove(entity: String, value: V) {
        val ent = hashMap[entity.lowercase()] ?: run {
            return
        }
        ent.remove(value)
        hashMap[entity.lowercase()] = ent
        toUpdate.add(entity.lowercase())
    }

    override fun remove(entity: Player) {
        remove(entity.name)
    }

    override fun remove(entity: String) {
        hashMap[entity.lowercase()] = null
        toUpdate.add(entity.lowercase())
    }

    override fun update() {
        save(toUpdate.toImmutableList())
    }

    private fun save(list: List<String>) {

        val currentHash = hashMap.toImmutableMap()

        for (i in list) {
            val tab = table.select { primaryColumn eq i }

            toUpdate.remove(i)

            val value = currentHash[i]

            if (tab.empty()) {
                if (value == null) continue
                table.insert {
                    it[primaryColumn] = i
                    it[column] = conv.convertToDatabase(value)
                }
            } else {
                if (value == null) {
                    table.deleteWhere { primaryColumn eq i }
                    continue
                }
                table.update({ primaryColumn eq i }) {
                    it[column] = conv.convertToDatabase(value)
                }

            }
        }
    }

    override fun load() {
        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = conv.convertToCache(i[column]) ?: ArrayList()
        }
    }

    override fun unload() {
        save(toUpdate.toImmutableList())
    }

}