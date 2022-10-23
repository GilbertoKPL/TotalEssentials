package github.gilbertokpl.total.cache

import github.gilbertokpl.base.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.serializer.LocationSerializer
import github.gilbertokpl.total.database.WarpsDataSQL
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object WarpData : CacheBase {
    override var table: Table = WarpsDataSQL
    override var primaryColumn: Column<String> = WarpsDataSQL.warpNameTable

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()

    val warpLocation = ins.location(this, WarpsDataSQL.warpLocationTable, LocationSerializer())

    fun checkIfWarpExist(warp: String) : Boolean {
        return warpLocation[warp] != null
    }

    fun getList(p: Player?): List<String> {
        val list = warpLocation.getMap().map { it.key }
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

    fun delete(entity: String) {
        warpLocation.delete(entity)
    }
}