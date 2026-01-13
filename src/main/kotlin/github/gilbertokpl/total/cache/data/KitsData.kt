package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.serializer.ItemSerializer
import github.gilbertokpl.total.cache.sql.KitsDataSQL
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

object KitsData : ICache {
    override var table: Table = KitsDataSQL
    override var primaryColumn: Column<String> = KitsDataSQL.kitNameTable

    private val ins = TotalEssentials.getCore().getCache()

    val kitFakeName = ins.string(this, KitsDataSQL.kitFakeNameTable)
    val kitTime = ins.long(this, KitsDataSQL.kitTimeTable)
    val kitItems = ins.list(this, KitsDataSQL.kitItemsTable, ItemSerializer())
    val kitWeight = ins.int(this, KitsDataSQL.kitWeightTable)

    fun checkIfExist(entity: String): Boolean {
        return kitTime[entity.lowercase()] != null
    }

    fun createNewKitData(entity: String) {
        val key = entity.lowercase()
        kitFakeName[key] = ""
        kitTime[key] = 0L
        kitItems[key] = arrayListOf()
        kitWeight[key] = 0
    }

    fun delete(entity: String) {
        val key = entity.lowercase()
        kitFakeName.remove(key)
        kitTime.remove(key)
        kitItems.remove(key)
        kitWeight.remove(key)
    }
}