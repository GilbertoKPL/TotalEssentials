package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.ItemSerializer
import github.gilbertokpl.total.cache.sql.VipDataSQL
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object VipData : CacheBase  {
    override var table: Table = VipDataSQL
    override var primaryColumn: Column<String> = VipDataSQL.vipName

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()


    val vipItems = ins.stringList(this, VipDataSQL.vipItems, ItemSerializer())
    val vipPrice = ins.integer(this, VipDataSQL.vipPrice)
    val vipQuantity = ins.integer(this, VipDataSQL.vipQuantity)
    val vipGroup = ins.string(this, VipDataSQL.vipGroup)
    val vipDiscord = ins.integer(this, VipDataSQL.vipDiscord)

    fun createNewVip(vipName: String, group: String) {
        vipItems[vipName] = ArrayList()
        vipPrice[vipName] = 0
        vipQuantity[vipName] = 0
        vipDiscord[vipName] = 0
        vipGroup[vipName] = group
    }

    fun checkIfVipExist(entity: String) : Boolean {
        return vipPrice[entity.lowercase()] != null
    }

}