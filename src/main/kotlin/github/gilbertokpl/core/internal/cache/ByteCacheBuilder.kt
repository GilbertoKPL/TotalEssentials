package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.json.JSONObject
import java.io.File
import java.nio.file.Files

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
        if (toUpdate.isEmpty) return
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
                        table.insert {
                            it[primaryColumn] = i
                            it[column] = value
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
        jsonPath = "./${corePlugin.mainPath}/sql/internal/ByteCacheBuilder.json"

        val file = File(jsonPath)

        if (file.exists()) {
            toUpdate = JSONObject(file.readText())

            for (i in toUpdate.toMap()) {
                hashMap[i.key] = if (i.value == "") null else i.value as T
            }
        } else {
            File("./${corePlugin.mainPath}/sql/internal").mkdirs()
        }

        for (i in table.selectAll()) {
            hashMap[i[primaryColumn]] = i[column]
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
