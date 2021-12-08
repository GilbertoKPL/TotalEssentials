package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

class OfflinePlayerData(player: OfflinePlayer) {
    private val uuid = CompletableFuture.supplyAsync { PluginUtil.getInstance().getPlayerUUID(player) }.get()

    fun checkExist(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                if (query.empty()) {
                    future.complete(false)
                    return@transaction
                } else {
                    future.complete(true)
                }
            }
        }
        return future
    }

    fun getHomeList(): CompletableFuture<List<String>?> {
        val future = CompletableFuture<List<String>?>()
        lateinit var homesList: String
        val cacheHomes = ArrayList<String>()
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                if (query.empty()) {
                    future.complete(null)
                    return@transaction
                }
                homesList = query.single()[PlayerDataSQL.savedHomes]
            }
        }
        for (h in homesList.split("|")) {
            if (h == "") continue
            val split = h.split(".")
            val nameHome = split[0]
            cacheHomes.add(nameHome)
        }
        future.complete(cacheHomes)
        return future
    }

    fun getLocationOfHome(home: String): CompletableFuture<Location?> {
        val future = CompletableFuture<Location?>()
        TaskUtil.getInstance().asyncExecutor {
            var bol = false
            lateinit var homesList: String
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    homesList = query.single()[PlayerDataSQL.savedHomes]
                }
            }
            for (h in homesList.split("|")) {
                if (h == "") continue
                val split = h.split(".")
                if (home.lowercase() == split[0]) {
                    future.complete(LocationUtil.getInstance().locationSerializer(split[1]))
                    bol = true
                    break
                }
            }
            if (!bol) {
                future.complete(null)
            }
        }
        return future
    }

    fun delHome(name: String) {
        TaskUtil.getInstance().asyncExecutor {
            lateinit var homes: String
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    homes = query.single()[PlayerDataSQL.savedHomes]
                }
            }

            var newHome = ""

            for (h in homes.split("|")) {
                if (h.split(".")[0] == name) continue
                if (newHome == "") {
                    newHome += h
                    continue
                }
                newHome += "|$h"
            }

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[savedHomes] = newHome
                }
            }
        }
    }

    fun createHome(name: String, loc: Location) {
        TaskUtil.getInstance().asyncExecutor {
            lateinit var homes: String
            val serializedHome = LocationUtil.getInstance().locationSerializer(loc)

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    homes = query.single()[PlayerDataSQL.savedHomes]
                }
            }
            var newHome = "$name.$serializedHome"

            for (h in homes.split("|")) {
                if (h == "") continue
                newHome += "|$h"
            }

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[savedHomes] = newHome
                }
            }
        }
    }
}