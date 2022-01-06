package github.genesyspl.cardinal.data.start

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.EColor
import github.genesyspl.cardinal.manager.IInstance
import github.genesyspl.cardinal.tables.SpawnDataSQL
import github.genesyspl.cardinal.tables.WarpsDataSQL
import github.genesyspl.cardinal.util.LocationUtil
import github.genesyspl.cardinal.util.PluginUtil
import github.genesyspl.cardinal.util.SqlUtil
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class LocationsLoader {
    fun loadSpawnCache() {
        DataManager.getInstance().spawnCache.clear()

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
                    EColor.YELLOW.color + github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalWorldNotExistSpawn + EColor.RESET.color
                )
                continue
            }
            DataManager.getInstance().spawnCache[it.key] = loc
        }
    }

    fun loadWarpCache() {
        DataManager.getInstance().warpsCache.clear()

        val hashWarp = HashMap<String, String>(40)

        transaction(SqlUtil.getInstance().sql) {
            for (values in WarpsDataSQL.selectAll()) {
                hashWarp[values[WarpsDataSQL.warpName]] = values[WarpsDataSQL.warpLocation]
            }
        }

        for (it in hashWarp) {
            val loc = LocationUtil.getInstance().locationSerializer(it.value)
            if (loc == null) {
                PluginUtil.getInstance().consoleMessage(
                    EColor.YELLOW.color + github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalWorldNotExistWarp.replace(
                        "%warp%",
                        it.key
                    ) + EColor.RESET.color
                )
                continue
            }
            DataManager.getInstance().warpsCache[it.key] = loc
        }
    }

    companion object : IInstance<LocationsLoader> {
        private val instance = createInstance()
        override fun createInstance(): LocationsLoader = LocationsLoader()
        override fun getInstance(): LocationsLoader {
            return instance
        }
    }

}