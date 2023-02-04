package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import okhttp3.internal.toImmutableList
import okhttp3.internal.toImmutableMap
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal class ByteCacheBuilder<T>(t: Table, pc: Column<String>, c: Column<T>) : CacheBuilder<T> {

    private val table = t

    private val column = c

    private val primaryColumn = pc

    private val hashMap = HashMap<String, T?>()

    private val toUpdate = ArrayList<String>()

    private val toUnload = ArrayList<String>()

    override fun getMap(): Map<String, T?> {
        return hashMap.toImmutableMap()
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
        toUpdate.add(entity.lowercase())
    }

    override operator fun set(entity: Player, value: T) {
        set(entity.name, value)
    }

    override operator fun set(entity: String, value: T) {
        hashMap[entity.lowercase()] = value
        toUpdate.add(entity.lowercase())
    }

    private fun save(list: List<String>) {
        val currentHash = hashMap.toImmutableMap()
        for (i in list) {
            toUpdate.remove(i)
            val tab = table.select { primaryColumn eq i }
            val value = currentHash[i]
            if (tab.empty()) {
                if (value == null) continue
                table.insert {
                    it[primaryColumn] = i
                    it[column] = value
                }
            } else {
                if (value == null) {
                    table.deleteWhere { primaryColumn eq i }
                    continue
                }
                table.update({ primaryColumn eq i }) {
                    it[column] = value
                }
            }
        }
    }

    override fun update() {
        save(toUpdate.toImmutableList())
    }

    override fun load() {
        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = i[column]
        }
    }

    override fun unload() {
        save(toUnload.toImmutableList())
    }

}