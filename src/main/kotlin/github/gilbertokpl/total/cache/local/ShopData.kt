package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.ShopDataSQL
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ShopData : CacheBase {
    override var table: Table = ShopDataSQL
    override var primaryColumn: Column<String> = ShopDataSQL.playerTable

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()

    val shopVisits = ins.integer(this, ShopDataSQL.visits)
    val shopLocation = ins.location(this, ShopDataSQL.location, LocationSerializer())
    val shopOpen = ins.boolean(this, ShopDataSQL.open)

    fun createNewShop(player: Player, location: Location) {
        shopVisits[player] = 0
        shopLocation[player] = location
        shopOpen[player] = true
    }

    fun checkIfExist(entity: String): Boolean {
        return shopVisits[entity.lowercase()] != null
    }
}