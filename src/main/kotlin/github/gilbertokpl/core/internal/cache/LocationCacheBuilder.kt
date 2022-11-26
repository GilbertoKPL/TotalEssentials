package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.convert.SerializatorBase
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import github.gilbertokpl.core.external.utils.Executor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.concurrent.TimeUnit

internal class LocationCacheBuilder(
    t: Table,
    pc: Column<String>,
    c: Column<String>,
    classConverter: SerializatorBase<Location?, String>
) : CacheBuilder<Location?> {

    private val conv = classConverter

    private val table = t

    private val column = c

    private val primaryColumn = pc

    private val hashMap = HashMap<String, Location?>()

    private val toUpdate = ArrayList<String>()

    private var inUpdate = false

    override fun getMap(): HashMap<String, Location?> {
        return hashMap
    }

    override operator fun get(entity: String): Location? {
        return hashMap[entity.lowercase()]
    }

    override operator fun get(entity: Player): Location? {
        return hashMap[entity.name.lowercase()]
    }

    override operator fun set(entity: Player, value: Location?) {
        set(entity.name, value)
    }

    override operator fun set(entity: String, value: Location?) {
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
                if (value == null) continue
                table.insert {
                    it[primaryColumn] = i
                    it[column] = conv.convertToDatabase(hashMap[i]!!)
                }
            } else {
                if (value == null) {
                    table.deleteWhere { primaryColumn eq i }
                    continue
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
            val location = conv.convertToCache(i[column]) ?: return
            hashMap[i[primaryColumn]] = location
        }
    }

}