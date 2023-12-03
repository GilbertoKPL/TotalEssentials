package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.ItemSerializer
import github.gilbertokpl.total.cache.sql.KitsDataSQL
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object KitsData : CacheBase {
    override var table: Table = KitsDataSQL
    override var primaryColumn: Column<String> = KitsDataSQL.kitNameTable

    private val ins = github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getCache()

    val kitFakeName = ins.string(this, KitsDataSQL.kitFakeNameTable)
    val kitTime = ins.long(this, KitsDataSQL.kitTimeTable)
    val kitItems = ins.list(this, KitsDataSQL.kitItemsTable, ItemSerializer())
    val kitWeight = ins.integer(this, KitsDataSQL.kitWeightTable)

    fun checkIfExist(entity: String): Boolean {
        return kitTime[entity.lowercase()] != null
    }

    fun createNewKitData(entity: String) {
        kitFakeName[entity] = ""
        kitTime[entity] = 0L
        kitItems[entity] = arrayListOf()
        kitWeight[entity] = 0
    }

    fun delete(entity: String) {
        kitFakeName.remove(entity)
        kitTime.remove(entity)
        kitItems.remove(entity)
        kitWeight.remove(entity)
    }
}