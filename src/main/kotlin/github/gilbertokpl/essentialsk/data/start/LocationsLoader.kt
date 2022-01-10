package github.gilbertokpl.essentialsk.data.start

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.tables.SpawnDataSQL
import github.gilbertokpl.essentialsk.tables.WarpsDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object LocationsLoader {
    fun loadSpawnCache() {
        DataManager.spawnCache.clear()

        val hashSpawn = HashMap<String, String>(2)

        transaction(SqlUtil.sql) {
            for (values in SpawnDataSQL.selectAll()) {
                hashSpawn[values[SpawnDataSQL.spawnName]] = values[SpawnDataSQL.spawnLocation]
            }
        }

        for (it in hashSpawn) {
            val loc = LocationUtil.locationSerializer(it.value)
            if (loc == null) {
                PluginUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.generalWorldNotExistSpawn + EColor.RESET.color
                )
                continue
            }
            DataManager.spawnCache[it.key] = loc
        }
    }

    fun loadWarpCache() {
        DataManager.warpsCache.clear()

        val hashWarp = HashMap<String, String>(40)

        transaction(SqlUtil.sql) {
            for (values in WarpsDataSQL.selectAll()) {
                hashWarp[values[WarpsDataSQL.warpName]] = values[WarpsDataSQL.warpLocation]
            }
        }

        for (it in hashWarp) {
            val loc = LocationUtil.locationSerializer(it.value)
            if (loc == null) {
                PluginUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.generalWorldNotExistWarp.replace(
                        "%warp%",
                        it.key
                    ) + EColor.RESET.color
                )
                continue
            }
            DataManager.warpsCache[it.key] = loc
        }
    }
}
