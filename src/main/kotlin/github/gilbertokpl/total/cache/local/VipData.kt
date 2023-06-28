package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.CommandsSerializer
import github.gilbertokpl.total.cache.serializer.ItemSerializer
import github.gilbertokpl.total.cache.sql.VipDataSQL
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object VipData : CacheBase {
    override var table: Table = VipDataSQL
    override var primaryColumn: Column<String> = VipDataSQL.vipName

    private val cache = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()

    val vipItems = cache.list(this, VipDataSQL.vipItems, ItemSerializer())
    val vipPrice = cache.integer(this, VipDataSQL.vipPrice)
    val vipQuantity = cache.integer(this, VipDataSQL.vipQuantity)
    val vipGroup = cache.string(this, VipDataSQL.vipGroup)
    val vipDiscord = cache.long(this, VipDataSQL.vipDiscord)
    val vipCommands = cache.list(this, VipDataSQL.vipCommands, CommandsSerializer())

    fun createNewVip(vipName: String, group: String, items: List<ItemStack> = emptyList(), price: Int = 0, quantity: Int = 0, discordId: Long = 0L, commands: List<String> = emptyList()) {
        vipItems[vipName] = ArrayList(items)
        vipPrice[vipName] = price
        vipQuantity[vipName] = quantity
        vipDiscord[vipName] = discordId
        vipGroup[vipName] = group
        vipCommands[vipName] = ArrayList(commands)
    }

    fun vipExists(vipName: String): Boolean {
        return vipPrice[vipName.lowercase()] != null
    }

}