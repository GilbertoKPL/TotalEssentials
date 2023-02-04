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

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()

    val spawnLocation = ins.location(this, SpawnDataSQL.spawnLocationTable, LocationSerializer())

    fun teleport(p: Player) {
        val loc = spawnLocation["spawn"] ?: run {
            if (p.hasPermission("*")) {
                p.sendMessage(LangConfig.spawnNotSet)
            }
            return
        }

        p.teleport(loc)
    }
}