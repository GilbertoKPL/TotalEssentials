package github.genesyspl.cardinal.data.`object`

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.tables.WarpsDataSQL
import github.genesyspl.cardinal.tables.WarpsDataSQL.warpName
import github.genesyspl.cardinal.util.LocationUtil
import github.genesyspl.cardinal.util.SqlUtil
import github.genesyspl.cardinal.util.TaskUtil
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
        DataManager.getInstance().warpsCache[name].also {
            if (it == null) {
                return true
            }
            return false
        }
    }

    fun getWarpList(p: Player?): List<String> {
        val list = DataManager.getInstance().warpsCache.map { it.key }
        if (p != null) {
            val newList = ArrayList<String>()
            list.forEach {
                if (p.hasPermission("cardinal.commands.warp.$it")) {
                    newList.add(it)
                }
            }
            return newList
        }
        return list
    }

    fun getLocation(): Location {
        return DataManager.getInstance().warpsCache[name]!!
    }

    fun setWarp(location: Location, s: CommandSender? = null) {
        //cache
        DataManager.getInstance().warpsCache[name] = location

        val loc = LocationUtil.getInstance().locationSerializer(location)

        //sql
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                WarpsDataSQL.insert {
                    it[warpName] = name
                    it[warpLocation] = loc
                }
            }
            s?.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsWarpCreated.replace(
                    "%warp%",
                    name
                )
            )
        }
    }

    fun delWarp(s: CommandSender? = null) {
        //cache
        DataManager.getInstance().warpsCache.remove(name)

        //sql
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                WarpsDataSQL.select { warpName eq name }.also { query ->
                    if (!query.empty()) {
                        WarpsDataSQL.deleteWhere { warpName eq name }
                        return@transaction
                    }
                }
            }
            s?.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().warpsWarpRemoved.replace(
                    "%warp%",
                    name
                )
            )
        }
    }
}