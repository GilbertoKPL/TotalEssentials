package github.gilbertokpl.total.cache.data.test

import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.serializer.ItemSerializer
import github.gilbertokpl.total.cache.sql.LimiterSQL
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

object LimitData : ICache {
    override var table: Table = LimiterSQL
    override var primaryColumn: Column<String> = LimiterSQL.groupTable

    private val cache = TotalEssentials.getCore().getCache()

    val limitItems = cache.list(this, LimiterSQL.itemsTable, ItemSerializer())
    val limitPrice = cache.integer(this, LimiterSQL.priceTable)

}