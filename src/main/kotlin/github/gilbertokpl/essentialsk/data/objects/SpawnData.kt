package github.gilbertokpl.essentialsk.data.objects

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SpawnData(spawnName: String) {

    private val name = spawnName.lowercase()

    fun checkCache(): Boolean {
        DataManager.spawnCache[name].also {
            if (it == null) {
                return true
            }
            return false
        }
    }

    fun getLocation(): Location {
        return DataManager.spawnCache[name]!!
    }

    fun setSpawn(location: Location, s: CommandSender? = null) {
        //cache
        DataManager.spawnCache[name] = location

        val loc = LocationUtil.locationSerializer(location)

        //sql
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
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
            s?.sendMessage(GeneralLang.spawnSendSetMessage)
        }
    }
}
