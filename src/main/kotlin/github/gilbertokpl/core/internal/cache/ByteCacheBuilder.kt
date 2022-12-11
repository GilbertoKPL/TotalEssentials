package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import github.gilbertokpl.core.external.utils.Executor
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.concurrent.TimeUnit

internal class ByteCacheBuilder<T>(t: Table, pc: Column<String>, c: Column<T>) : CacheBuilder<T> {

    private val table = t

    private val column = c

    private val primaryColumn = pc

    private val hashMap = HashMap<String, T?>()

    private val toUpdate = ArrayList<String>()

    private var inUpdate = false

    override fun getMap(): HashMap<String, T?> {
        return hashMap
    }

    override operator fun get(entity: String): T? {
        return hashMap[entity.lowercase()]
    }

    override operator fun get(entity: Player): T? {
        return hashMap[entity.name.lowercase()]
    }

    override fun set(entity: String, value: T, override: Boolean) {
        set(entity, value)
    }

    override fun delete(entity: Player) {
        delete(entity.name)
    }

    override fun delete(entity: String) {
        hashMap[entity.lowercase()] = null
    }

    override operator fun set(entity: Player, value: T) {
        set(entity.name, value)
    }

    override operator fun set(entity: String, value: T) {
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
    }

    override fun update() {
        if (inUpdate) return
        inUpdate = true
        for (i in toUpdate) {
            val tab = table.select { primaryColumn eq i }
            val value = hashMap[i]
            if (tab.empty()) {
                if (value == null) continue
                table.insert {
                    it[primaryColumn] = i
                    it[column] = hashMap[i]!!
                }
            } else {
                if (value == null) {
                    table.deleteWhere { primaryColumn eq i }
                    continue
                }
                table.update({ primaryColumn eq i }) {
                    it[column] = hashMap[i]!!
                }
            }
        }
        toUpdate.clear()
        inUpdate = false
    }

    override fun load() {
        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = i[column]
        }
    }

}