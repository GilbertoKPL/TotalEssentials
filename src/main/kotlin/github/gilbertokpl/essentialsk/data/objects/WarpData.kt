package github.gilbertokpl.essentialsk.data.objects

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.tables.WarpsDataSQL
import github.gilbertokpl.essentialsk.tables.WarpsDataSQL.warpName
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class WarpData(warpName: String) {

    private val name = warpName.lowercase()

    fun checkCache(): Boolean {
        DataManager.warpsCache[name].also {
            if (it == null) {
                return true
            }
            return false
        }
    }

    fun getWarpList(p: Player?): List<String> {
        val list = DataManager.warpsCache.map { it.key }
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

    fun getLocation(): Location {
        return DataManager.warpsCache[name]!!
    }

    fun setWarp(location: Location, s: CommandSender? = null) {
        //cache
        DataManager.warpsCache[name] = location

        val loc = LocationUtil.locationSerializer(location)

        //sql
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
                WarpsDataSQL.insert {
                    it[warpName] = name
                    it[warpLocation] = loc
                }
            }
            s?.sendMessage(GeneralLang.warpsWarpCreated.replace("%warp%", name))
        }
    }

    fun delWarp(s: CommandSender? = null) {
        //cache
        DataManager.warpsCache.remove(name)

        //sql
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
                WarpsDataSQL.select { warpName eq name }.also { query ->
                    if (!query.empty()) {
                        WarpsDataSQL.deleteWhere { warpName eq name }
                        return@transaction
                    }
                }
            }
            s?.sendMessage(GeneralLang.warpsWarpRemoved.replace("%warp%", name))
        }
    }
}
