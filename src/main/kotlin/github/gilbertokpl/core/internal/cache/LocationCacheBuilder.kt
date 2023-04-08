package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.convert.SerializerBase
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import okhttp3.internal.toImmutableList
import okhttp3.internal.toImmutableMap
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*

internal class LocationCacheBuilder(
    t: Table,
    pc: Column<String>,
    c: Column<String>,
    classConverter: SerializerBase<Location?, String>
) : CacheBuilder<Location?> {

    private val conv = classConverter

    private val table = t

    private val column = c

    private val primaryColumn = pc

    private val hashMap = HashMap<String, Location?>()

    private val toUpdate = ArrayList<String>()

    override fun getMap(): Map<String, Location?> {
        return hashMap.toImmutableMap()
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

    override fun set(entity: String, value: Location?, override: Boolean) {
        set(entity, value)
    }

    override operator fun set(entity: String, value: Location?) {
        hashMap[entity.lowercase()] = value
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

            toUpdate.remove(i)

            val tab = table.select { primaryColumn eq i }

            val value = currentHash[i]

            if (tab.empty()) {
                if (value == null) continue
                table.insert {
                    it[primaryColumn] = i
                    it[column] = conv.convertToDatabase(value)
                }
            } else {
                if (value == null) {
                    table.update({ primaryColumn eq i }) {
                        it[column] = ""
                    }
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
            val location = conv.convertToCache(i[column]) ?: continue
            hashMap[i[primaryColumn]] = location
        }
    }

    override fun unload() {
        save(toUpdate.toImmutableList())
    }

}