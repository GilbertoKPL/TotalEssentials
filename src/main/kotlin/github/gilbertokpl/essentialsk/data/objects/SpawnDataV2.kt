package github.gilbertokpl.essentialsk.data.objects

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object SpawnDataV2 {
    private val spawnCache = HashMap<String, Location>(2)

    operator fun get(warp: String) = spawnCache[warp.lowercase()]

    fun set(spawn: String, location: Location, s: CommandSender? = null) {
        //cache
        spawnCache[spawn.lowercase()] = location

        val loc = LocationUtil.locationSerializer(location)

        //sql
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
                if (SpawnDataSQL.select { SpawnDataSQL.spawnName eq spawn.lowercase() }.empty()) {
                    SpawnDataSQL.insert {
                        it[spawnName] = spawn.lowercase()
                        it[spawnLocation] = loc
                    }
                    return@transaction
                }
                SpawnDataSQL.update({ SpawnDataSQL.spawnName eq spawn.lowercase() }) {
                    it[spawnLocation] = loc
                }
            }
            s?.sendMessage(GeneralLang.spawnSendSetMessage)
        }
    }

    fun loadSpawnCache() {
        spawnCache.clear()

        val hashSpawn = HashMap<String, String>(2)

        transaction(SqlUtil.sql) {
            for (values in SpawnDataSQL.selectAll()) {
                hashSpawn[values[SpawnDataSQL.spawnName]] = values[SpawnDataSQL.spawnLocation]
            }
        }

        for (it in hashSpawn) {
            val loc = LocationUtil.locationSerializer(it.value)
            if (loc == null) {
                MainUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.generalWorldNotExistSpawn + EColor.RESET.color
                )
                continue
            }
            spawnCache[it.key] = loc
        }
    }

}
