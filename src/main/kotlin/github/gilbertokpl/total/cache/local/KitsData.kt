package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.ItemSerializer
import github.gilbertokpl.total.cache.sql.KitsDataSQL
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object KitsData : CacheBase {
    override var table: Table = KitsDataSQL
    override var primaryColumn: Column<String> = KitsDataSQL.kitNameTable

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()

    val kitFakeName = ins.string(this, KitsDataSQL.kitFakeNameTable)
    val kitTime = ins.long(this, KitsDataSQL.kitTimeTable)
    val kitItems = ins.stringList(this, KitsDataSQL.kitItemsTable, ItemSerializer())
    val kitWeight = ins.integer(this, KitsDataSQL.kitWeightTable)

    fun checkIfExist(entity: String): Boolean {
        return kitTime[entity.lowercase()] != null
    }

    fun createNewKitData(entity: String) {
        kitFakeName[entity] = ""
        kitTime[entity] = 0
        kitItems[entity] = ArrayList()
        kitWeight[entity] = 0
    }

    fun delete(entity: String) {
        kitFakeName.delete(entity)
        kitTime.delete(entity)
        kitItems.delete(entity)
        kitWeight.delete(entity)
    }
}