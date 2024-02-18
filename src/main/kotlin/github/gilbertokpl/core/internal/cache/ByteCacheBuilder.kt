package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import org.bukkit.entity.Player
import org.checkerframework.checker.units.qual.K
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.sql.SQLIntegrityConstraintViolationException

internal class ByteCacheBuilder<T>(
    private val table: Table,
    private val primaryColumn: Column<String>,
    private val column: Column<T>
) : CacheBuilder<T> {

    private val hashMap = mutableMapOf<String, T?>()

    private var toUpdate = JSONObject()
    private var jsonPath = ""

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
        toUpdate.put(entity.lowercase(), value)
        saveJson()
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
        toUpdate.put(entity.lowercase(), "")
        saveJson()
    }

    private fun save(list: Set<String>) {
        if (toUpdate.isEmpty) {
            saveJson()
            return
        }
        val existingRows = table.select { primaryColumn inList toUpdate.keySet() }.toList().associateBy { it[primaryColumn] }

        for (i in list) {
            if (i in toUpdate.keySet()) {
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
                                it[column] = value
                            }
                        } catch (sql : SQLIntegrityConstraintViolationException) {
                            table.update({ primaryColumn eq i }) {
                                it[column] = value
                            }
                        }
                    } else {
                        table.update({ primaryColumn eq i }) {
                            it[column] = value
                        }
                    }
                }
            }
        }
        saveJson()
    }

    override fun update() {
        save(toUpdate.keySet())
    }

    override fun load(corePlugin: CorePlugin) {
        jsonPath = "./${corePlugin.mainPath}/sql/internal/ByteCacheBuilder-${column.name.lowercase()}.json"

        for (row in table.selectAll()) {
            hashMap[row[primaryColumn]] = row[column]
        }

        val file = File(jsonPath)

        if (file.exists()) {
            val jsonString = file.readText()
            val jsonObject = JSONObject(jsonString)

            for (key in jsonObject.keys()) {
                val jsonValue = jsonObject[key]

                val value = when (column.columnType) {
                    IntegerColumnType() -> jsonValue.toString().toInt() as T
                    LongColumnType() -> jsonValue.toString().toLong() as T
                    DoubleColumnType() -> jsonValue.toString().toDouble() as T
                    else -> jsonValue as T
                }

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
        }
        else {
            File(jsonPath).writeText(toUpdate.toString())
        }
    }
}
