package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.del
import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.WarpsDataSQL
import github.gilbertokpl.essentialsk.data.tables.WarpsDataSQL.warpName
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.serializator.internal.LocationSerializer
import github.gilbertokpl.essentialsk.util.MainUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

internal object WarpData {
    //warps
    private val warpsCache = mutableMapOf<String, Location>()

    operator fun get(warp: String) = warpsCache[warp.lowercase()]

    fun set(warp: String, location: Location) {
        //cache
        warpsCache[warp.lowercase()] = location

        val loc = LocationSerializer.serialize(location)

        WarpsDataSQL.put(warp.lowercase(), hashMapOf(WarpsDataSQL.warpLocation to loc))

    }

    fun del(warp: String) {
        //cache
        warpsCache.remove(warp.lowercase())

        WarpsDataSQL.del(warp.lowercase())
    }

    fun getList(p: Player?): List<String> {
        val list = warpsCache.map { it.key }
        if (p != null) {
            val newList = ArrayList<String>()
            list.forEach {
                if (p.hasPermission("essentialsk.commands.warp.$it")) {
                    newList.add(it)
                }
            }
            return newList
        }
        return list
    }

    fun loadWarpCache() {
        warpsCache.clear()

        val hashWarp = HashMap<String, String>(40)

        transaction(DataManager.sql) {
            for (values in WarpsDataSQL.selectAll()) {
                hashWarp[values[warpName]] = values[WarpsDataSQL.warpLocation]
            }
        }

        for (it in hashWarp) {
            val loc = LocationSerializer.deserialize(it.value)
            if (loc == null) {
                MainUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.generalWorldNotExistWarp.replace(
                        "%warp%",
                        it.key
                    ) + EColor.RESET.color
                )
                continue
            }
            warpsCache[it.key] = loc
        }
    }
}
