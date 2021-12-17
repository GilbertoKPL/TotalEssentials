package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

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
            Dao.getInstance().spawnCache[it.key] = LocationUtil.getInstance().locationSerializer(it.value)
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

    fun setSpawn(location: Location): Boolean {
        //cache
        Dao.getInstance().spawnCache[name] = location

        val loc = LocationUtil.getInstance().locationSerializer(location)

        //sql
        return CompletableFuture.supplyAsync({
            try {
                transaction(SqlUtil.getInstance().sql) {
                    if (SpawnDataSQL.select { SpawnDataSQL.spawnName eq name }.empty()) {
                        SpawnDataSQL.insert {
                            it[spawnName] = name
                            it[spawnLocation] = loc
                        }
                        return@transaction
                    }
                    SpawnDataSQL.update ({SpawnDataSQL.spawnName eq name }) {
                        it[spawnLocation] = loc
                    }
                }
                return@supplyAsync true
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
                return@supplyAsync false
            }
        }, TaskUtil.getInstance().getExecutor()).get()
    }

}