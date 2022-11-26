package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.convert.SerializatorBase
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilderV2
import github.gilbertokpl.core.external.utils.Executor
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import java.util.concurrent.TimeUnit

internal class ListCacheBuilder<K, V>(
    t: Table,
    pc: Column<String>,
    c: Column<K>,
    classConverter: SerializatorBase<ArrayList<V>, K>
) :
    CacheBuilderV2<ArrayList<V>, V> {

    private val conv = classConverter

    private val table = t

    private val column = c

    private val primaryColumn = pc

    private val hashMap = HashMap<String, ArrayList<V>?>()

    override fun getMap(): HashMap<String, ArrayList<V>?> {
        return hashMap
    }

    private val toUpdate = ArrayList<String>()

    private var inUpdate = false

    override operator fun get(entity: String): ArrayList<V>? {
        return hashMap[entity.lowercase()]
    }

    override operator fun get(entity: Player): ArrayList<V>? {
        return hashMap[entity.name.lowercase()]
    }

    override operator fun set(entity: Player, value: ArrayList<V>) {
        set(entity.name, value)
    }

    override operator fun set(entity: String, value: ArrayList<V>) {
        val ent = hashMap[entity.lowercase()] ?: run {
            hashMap[entity.lowercase()] = value
            if (!inUpdate) {
                toUpdate.add(entity.lowercase())
            } else {
                Executor.executor.scheduleWithFixedDelay({
                    if (!inUpdate) {
                        toUpdate.add(entity.lowercase())
                        Thread.currentThread().stop()
                    }
                }, 1, 1, TimeUnit.SECONDS)
            }
            return
        }
        for (i in value) {
            ent.add(i)
        }
        hashMap[entity.lowercase()] = ent
        if (!inUpdate) {
            toUpdate.add(entity.lowercase())
        } else {
            Executor.executor.scheduleWithFixedDelay({
                if (!inUpdate) {
                    toUpdate.add(entity.lowercase())
                    Thread.currentThread().stop()
                }
            }, 1, 1, TimeUnit.SECONDS)
        }
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
        while (!inUpdate) {
            toUpdate.add(entity.lowercase())
        }
    }

    override fun delete(entity: Player) {
        delete(entity.name)
    }

    override fun delete(entity: String) {
        hashMap[entity.lowercase()] = null
    }

    override fun update() {
        inUpdate = true
        for (i in toUpdate) {
            val tab = table.select { primaryColumn eq i }

            val value = hashMap[i]

            if (tab.empty()) {
                if (value == null) return
                table.insert {
                    it[primaryColumn] = i
                    it[column] = conv.convertToDatabase(hashMap[i]!!)
                }
            } else {
                if (value == null) {
                    table.deleteWhere { primaryColumn eq i }
                }
                table.update({ primaryColumn eq i }) {
                    it[column] = conv.convertToDatabase(hashMap[i]!!)
                }

            }
        }
        toUpdate.clear()
        inUpdate = false
    }

    override fun load() {
        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = conv.convertToCache(i[column])
        }
    }

}