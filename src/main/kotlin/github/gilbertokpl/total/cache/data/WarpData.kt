package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.interfaces.CacheBase
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.WarpsDataSQL
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

object WarpData : CacheBase {
    override var table: Table = WarpsDataSQL
    override var primaryColumn: Column<String> = WarpsDataSQL.warpNameTable
    private val cache = TotalEssentials.getCore().getCache()

    val warpLocation = cache.location(this, WarpsDataSQL.warpLocationTable, LocationSerializer())

    fun checkIfWarpExist(warpName: String): Boolean {
        return warpLocation[warpName] != null
    }

    fun getWarpList(player: Player?): List<String> {
        val warpNames = warpLocation.getMap().filterValues { it != null }.keys.toList()
        return if (player == null) {
            warpNames
        } else {
            warpNames.filter { player.hasPermission("totalessentials.commands.warp.$it") }
        }
    }

    fun deleteWarp(warpName: String) {
        warpLocation.remove(warpName)
    }
}