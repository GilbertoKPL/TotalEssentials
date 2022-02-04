package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.player.serializator.internal.LocationSerializer
import github.gilbertokpl.essentialsk.util.MainUtil
import org.bukkit.Location
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

internal object SpawnData {
    private val spawnCache = HashMap<String, Location>(2)

    operator fun get(warp: String) = spawnCache[warp.lowercase()]

    fun set(spawn: String, location: Location) {
        //cache
        spawnCache[spawn.lowercase()] = location

        val loc = LocationSerializer.serialize(location)

        SpawnDataSQL.put(spawn.lowercase(), hashMapOf(SpawnDataSQL.spawnLocation to loc))
    }

    fun loadSpawnCache() {
        spawnCache.clear()

        val hashSpawn = HashMap<String, String>(2)

        transaction(DataManager.sql) {
            for (values in SpawnDataSQL.selectAll()) {
                hashSpawn[values[SpawnDataSQL.spawnName]] = values[SpawnDataSQL.spawnLocation]
            }
        }

        for (it in hashSpawn) {
            val loc = LocationSerializer.deserialize(it.value)
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
