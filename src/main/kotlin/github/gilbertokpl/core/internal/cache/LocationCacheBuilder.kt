package github.gilbertokpl.core.internal.cache

import com.sun.jna.platform.unix.solaris.LibKstat.KstatNamed.UNION.STR
import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.cache.convert.SerializerBase
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.json.JSONObject
import java.io.File
import java.sql.SQLIntegrityConstraintViolationException

internal class LocationCacheBuilder(
    private val table: Table,
    private val primaryColumn: Column<String>,
    private val column: Column<String>,
    private val classConvert: SerializerBase<Location?, String>
) : CacheBuilder<Location?> {

    private val hashMap = HashMap<String, Location?>()
    private var toUpdate = JSONObject()
    private var jsonPath = ""

    override fun getMap(): Map<String, Location?> {
        return hashMap.toMap()
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
        toUpdate.put(entity.lowercase(), classConvert.convertToDatabase(value))
    }

    override fun remove(entity: Player) {
        remove(entity.name)
    }

    override fun remove(entity: String) {
        hashMap[entity.lowercase()] = null
        toUpdate.put(entity.lowercase(), "null") // String "null" para indicar nulo no JSON
    }

    override fun update() {
        save(toUpdate.keySet())
    }

    private fun save(list: Set<String>) {
        if (toUpdate.isEmpty) {
            saveJson()
            return
        }

        val existingRows = table.select { primaryColumn inList toUpdate.keySet() }.toList().associateBy { it[primaryColumn] }

        for (i in list) {
            toUpdate.remove(i)
            val value = hashMap[i]

            if (value == null) {
                existingRows[i]?.let { row ->
                    table.deleteWhere { primaryColumn eq row[primaryColumn] }
                }
            } else {
                if (existingRows[i] == null) {
                    try {
                        table.insert {
                            it[primaryColumn] = i
                            it[column] = classConvert.convertToDatabase(value)
                        }
                    } catch (sql: SQLIntegrityConstraintViolationException) {
                        table.update({ primaryColumn eq i }) {
                            it[column] = classConvert.convertToDatabase(value)
                        }
                    }
                } else {
                    table.update({ primaryColumn eq i }) {
                        it[column] = classConvert.convertToDatabase(value)
                    }
                }
            }
        }

        saveJson()
    }

    override fun load(corePlugin: CorePlugin) {
        jsonPath = "./${corePlugin.mainPath}/sql/internal/LocationCacheBuilder-${column.name.lowercase()}.json"

        for (row in table.selectAll()) {
            val location = classConvert.convertToCache(row[column])
            hashMap[row[primaryColumn]] = location
        }

        val file = File(jsonPath)

        if (file.exists()) {
            toUpdate = JSONObject(file.readText())

            toUpdate.keys().forEach { key ->
                val jsonValue = toUpdate[key]
                val value = if (jsonValue == "null") null else classConvert.convertToCache(jsonValue as String)
                hashMap[key.lowercase()] = value
            }
        } else {
            File("./${corePlugin.mainPath}/sql/internal").mkdirs()
        }
    }

    override fun unload() {
        save(toUpdate.keySet())
    }

    private fun saveJson() {
        if (toUpdate.isEmpty) {
            File(jsonPath).delete()
        } else {
            File(jsonPath).writeText(toUpdate.toString())
        }
    }
}


