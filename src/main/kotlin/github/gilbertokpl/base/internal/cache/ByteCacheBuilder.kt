package github.gilbertokpl.base.internal.cache

import github.gilbertokpl.base.external.cache.interfaces.CacheBuilder
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
            github.gilbertokpl.total.TotalEssentials.basePlugin.getTask().async {
                repeating(1)
                if (!inUpdate) {
                    toUpdate.add(entity.lowercase())
                    this.currentTask?.cancel()
                }
            }
        }
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