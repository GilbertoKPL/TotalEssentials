package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.ShopDataSQL
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

object ShopData : ICache {
    override var table: Table = ShopDataSQL
    override var primaryColumn: Column<String> = ShopDataSQL.playerTable

    private val cache = TotalEssentials.getCore().getCache()

    val shopVisits = cache.integer(this, ShopDataSQL.visits)
    val shopLocation = cache.location(this, ShopDataSQL.location, LocationSerializer())
    val shopOpen = cache.boolean(this, ShopDataSQL.open)

    fun createNewShop(location: Location, player: Player) {
        if (!checkIfShopExists(player.name.lowercase())) {
            shopVisits[player] = 0
        }
        shopLocation[player] = location
        shopOpen[player] = false
    }

    fun checkIfShopExists(playerName: String): Boolean {
        return shopVisits[playerName.lowercase()] != null
    }
}