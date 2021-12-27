package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SpawnData(spawnName: String) {

    private val name = spawnName.lowercase()

    fun loadSpawnCache() {
        Dao.getInstance().spawnCache.clear()

        val hashSpawn = HashMap<String, String>(2)

        transaction(SqlUtil.getInstance().sql) {
            for (values in SpawnDataSQL.selectAll()) {
                hashSpawn[values[SpawnDataSQL.spawnName]] = values[SpawnDataSQL.spawnLocation]
            }
        }

        for (it in hashSpawn) {
            val loc = LocationUtil.getInstance().locationSerializer(it.value)
            if (loc == null) {
                PluginUtil.getInstance().consoleMessage(
                    EColor.YELLOW.color + GeneralLang.getInstance().generalWorldNotExistSpawn + EColor.RESET.color
                )
                continue
            }
            Dao.getInstance().spawnCache[it.key] = loc
        }
    }

    fun checkCache(): Boolean {
        Dao.getInstance().spawnCache[name].also {
            if (it == null) {
                return true
            }
            return false
        }
    }

    fun getLocation(): Location {
        return Dao.getInstance().spawnCache[name]!!
    }

    fun setSpawn(location: Location, s: CommandSender? = null) {
        //cache
        Dao.getInstance().spawnCache[name] = location

        val loc = LocationUtil.getInstance().locationSerializer(location)

        //sql
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                if (SpawnDataSQL.select { SpawnDataSQL.spawnName eq name }.empty()) {
                    SpawnDataSQL.insert {
                        it[spawnName] = name
                        it[spawnLocation] = loc
                    }
                    return@transaction
                }
                SpawnDataSQL.update({ SpawnDataSQL.spawnName eq name }) {
                    it[spawnLocation] = loc
                }
            }
            s?.sendMessage(GeneralLang.getInstance().spawnSendSetMessage)
        }
    }

}