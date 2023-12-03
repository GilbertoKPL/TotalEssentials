package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.SpawnDataSQL
import github.gilbertokpl.total.config.files.LangConfig
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object SpawnData : CacheBase {
    override var table: Table = SpawnDataSQL
    override var primaryColumn: Column<String> = SpawnDataSQL.spawnNameTable

    private val cache = github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getCache()

    val spawnLocation = cache.location(this, SpawnDataSQL.spawnLocationTable, LocationSerializer())

    fun teleportToSpawn(player: Player) {
        val spawnLoc = spawnLocation["spawn"]
        if (spawnLoc != null) {
            player.teleport(spawnLoc)
        } else {
            if (player.hasPermission("*")) {
                player.sendMessage(LangConfig.spawnNotSet)
                spawnLocation["spawn"] = player.location
            }
        }
    }
}