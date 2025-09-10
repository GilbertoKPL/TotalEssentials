package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.SpawnDataSQL
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

object SpawnData : ICache {
    override var table: Table = SpawnDataSQL
    override var primaryColumn: Column<String> = SpawnDataSQL.spawnNameTable

    private val cache = TotalEssentials.getCore().getCache()

    val spawnLocation = cache.location(this, SpawnDataSQL.spawnLocationTable, LocationSerializer())

    fun teleportToSpawn(player: Player) {
        val spawnLoc = spawnLocation["spawn"]
        if (spawnLoc != null) {
            player.teleportSafe(spawnLoc)
        } else {
            if (player.hasPermission("*")) {
                player.sendMessage(LangConfig.spawnNotSet)
                spawnLocation["spawn"] = player.location
            }
        }
    }
}