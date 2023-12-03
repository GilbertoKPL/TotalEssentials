package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.WarpsDataSQL
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object WarpData : CacheBase {
    override var table: Table = WarpsDataSQL
    override var primaryColumn: Column<String> = WarpsDataSQL.warpNameTable
    private val cache = github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getCache()

    val warpLocation = cache.location(this, WarpsDataSQL.warpLocationTable, LocationSerializer())

    fun checkIfWarpExist(warpName: String): Boolean {
        return warpLocation[warpName] != null
    }

    fun getWarpList(player: Player?): List<String> {
        val warpNames = warpLocation.getMap().keys.toList()
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