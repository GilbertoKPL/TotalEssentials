package github.gilbertokpl.essentialsk.data.objects

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.tables.WarpsDataSQL
import github.gilbertokpl.essentialsk.tables.WarpsDataSQL.warpName
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object WarpDataV2 {
    //warps
    private val warpsCache = mutableMapOf<String, Location>()

    operator fun get(warp: String) = warpsCache[warp.lowercase()]

    fun set(warp: String, location: Location, s: CommandSender? = null) {
        //cache
        warpsCache[warp.lowercase()] = location

        val loc = LocationUtil.locationSerializer(location)

        //sql
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
                WarpsDataSQL.insert {
                    it[warpName] = warp.lowercase()
                    it[warpLocation] = loc
                }
            }
            s?.sendMessage(GeneralLang.warpsWarpCreated.replace("%warp%", warp.lowercase()))
        }
    }

    fun del(warp: String, s: CommandSender? = null) {
        //cache
        warpsCache.remove(warp.lowercase())

        //sql
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
                WarpsDataSQL.select { warpName eq warp.lowercase() }.also { query ->
                    if (!query.empty()) {
                        WarpsDataSQL.deleteWhere { warpName eq warp.lowercase() }
                        return@transaction
                    }
                }
            }
            s?.sendMessage(GeneralLang.warpsWarpRemoved.replace("%warp%", warp.lowercase()))
        }
    }

    fun getList(p: Player?): List<String> {
        val list = warpsCache.map { it.key }
        if (p != null) {
            val newList = ArrayList<String>()
            list.forEach {
                if (p.hasPermission("essentialsk.commands.warp.$it")) {
                    newList.add(it)
                }
            }
            return newList
        }
        return list
    }

    fun loadWarpCache() {
        warpsCache.clear()

        val hashWarp = HashMap<String, String>(40)

        transaction(SqlUtil.sql) {
            for (values in WarpsDataSQL.selectAll()) {
                hashWarp[values[warpName]] = values[WarpsDataSQL.warpLocation]
            }
        }

        for (it in hashWarp) {
            val loc = LocationUtil.locationSerializer(it.value)
            if (loc == null) {
                MainUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.generalWorldNotExistWarp.replace(
                        "%warp%",
                        it.key
                    ) + EColor.RESET.color
                )
                continue
            }
            warpsCache[it.key] = loc
        }
    }
}
