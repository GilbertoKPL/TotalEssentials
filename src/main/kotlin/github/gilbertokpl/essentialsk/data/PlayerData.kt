package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.kitsTime
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.savedHomes
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture


class PlayerData(player: Player) {

    private val uuid = PluginUtil.getInstance().getPlayerUUID(player)
    private val p = player

    fun unloadCache() {
        Dao.getInstance().playerCache.remove(uuid)
    }

    fun loadCache() {
        TaskUtil.getInstance().asyncExecutor {
            val cache = HashMap<String, Long>(40)
            lateinit var timeKits: String
            lateinit var fakeNick: String
            var emptyQuery = false

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    emptyQuery = query.empty()
                    if (emptyQuery) {
                        PlayerDataSQL.insert {
                            it[uuid] = this@PlayerData.uuid
                            it[kitsTime] = ""
                            it[FakeNick] = ""
                        }
                        return@transaction
                    }
                    timeKits = query.single()[kitsTime]
                    fakeNick = query.single()[FakeNick]
                }
            }

            //cache
            Dao.getInstance().playerCache[uuid] = InternalPlayerData(
                uuid,
                HashMap(40),
                HashMap(40),
                ""
            )

            if (emptyQuery) {
                return@asyncExecutor
            }

            var replace = ""
            var update = false

            for (kits in timeKits.split("-")) {
                try {
                    if (kits == "") continue
                    val split = kits.split(".")
                    val timeKit = split[1].toLong()
                    val nameKit = split[0]

                    val kitsCache = KitData(nameKit)

                    if (!kitsCache.checkCache()) {
                        update = true
                        continue
                    }

                    val timeAll = KitData(nameKit).getCache(true)

                    if (timeKit != 0L && timeAll != null && (timeAll.time + timeKit) > System.currentTimeMillis()) {
                        replace += if(replace == "") {
                            "$nameKit.$timeKit"
                        } else {
                            "-$nameKit.$timeKit"
                        }
                        cache[nameKit] = timeKit
                        continue
                    }
                } catch (ignored : Exception) {
                    update = true
                }
            }

            if (update) {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update {
                        it[kitsTime] = replace
                    }
                }
            }

            getCache()?.let { it.kitsCache = cache } ?: return@asyncExecutor

            //nick
            if (fakeNick != "") {
                getCache()?.let { it.FakeNick = fakeNick } ?: return@asyncExecutor
                p.setDisplayName(fakeNick)
            }

        }
    }

    fun delKitTime(kit: String) {
        val cache = getCache()?.kitsCache ?: return
        cache.remove(kit)
        getCache()?.let { it.kitsCache = cache } ?: return
    }

    fun setKitTime(kit: String, time: Long) {
        //cache
        val cache = getCache()?.kitsCache ?: return
        cache.remove(kit)
        cache[kit] = time
        getCache()?.let { it.kitsCache = cache } ?: return

        //sql
        TaskUtil.getInstance().asyncExecutor {

            var query = false
            lateinit var kitTime: String

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also {
                    query = it.empty()
                    kitTime = it.single()[kitsTime]
                }
            }

            if (query || kitTime == "") {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                        it[kitsTime] = "$kit.$time"
                    }
                }
                return@asyncExecutor
            }
            val check = kitTime.split("-")
            var newPlace = ""
            for (i in check) {
                if (i.split(".")[0] != kit) {
                    if (newPlace == "") {
                        newPlace += i
                        continue
                    }
                    newPlace += "-$i"
                    continue
                }
            }
            newPlace += if (newPlace == "") {
                "$kit.${System.currentTimeMillis()}"
            } else {
                "-$kit.$time"
            }
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = newPlace
                }
            }
        }
    }

    fun createHome(name: String, loc : Location) {
        //cache
        val cache = getCache()?.homeCache ?:return
        cache[name] = loc
        getCache()?.let { it.homeCache = cache } ?: return

        //sql
        TaskUtil.getInstance().asyncExecutor {
            lateinit var homes : String
            val serializedHome = LocationUtil.getInstance().locationSerializer(loc)
            var emptyQuery = false
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    emptyQuery = query.empty()
                    if (emptyQuery) {
                        PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                            it[savedHomes] = "$name.$serializedHome"
                        }
                        return@transaction
                    }
                    homes = query.single()[savedHomes]
                }
            }
            if (emptyQuery) return@asyncExecutor

            var newHome = "$name.$serializedHome"

            for (h in homes.split("-")) {
                newHome += "-$h"
            }

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[savedHomes] = newHome
                }
            }
        }
    }

    fun delHome(name: String) {
        //cache
        val cache = getCache()?.homeCache ?:return
        cache.remove(name)
        getCache()?.let { it.homeCache = cache } ?: return

        //sql
        TaskUtil.getInstance().asyncExecutor {
            lateinit var homes : String
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    homes = query.single()[savedHomes]
                }
            }

            var newHome = ""

            for (h in homes.split("-")) {
                if (h.split(".")[0] == name) continue
                if (newHome == "") {
                    newHome += h
                    continue
                }
                newHome += "-$h"
            }

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[savedHomes] = newHome
                }
            }
        }
    }

    fun setNick(newNick: String, color: Boolean) {
        if (color) {
            getCache()?.let { it.FakeNick = newNick } ?: return
            p.setDisplayName(newNick)
            TaskUtil.getInstance().asyncExecutor {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                        it[FakeNick] = newNick
                    }
                }
            }
        }
    }

    fun setNick(newNick: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        lateinit var players: List<String>
        transaction(SqlUtil.getInstance().sql) {
            players = PlayerDataSQL.selectAll().map { it[FakeNick] }
        }
        if (!MainConfig.getInstance().nicksCanPlayerHaveSameNick) {
            for (i in players) {
                if (i.equals(newNick, true)) {
                    println("")
                    future.complete(true)
                    break
                }
            }
        }
        p.setDisplayName(newNick)
        getCache()?.let { it.FakeNick = newNick } ?: future.complete(true)
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                it[FakeNick] = newNick
            }
            future.complete(false)
        }
        return future
    }

    fun removeNick() {
        //cache
        getCache()?.let { it.FakeNick = "" } ?: return
        p.setDisplayName(p.name)

        //sql
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = ""
                }
            }
        }
    }


    fun getCache(): InternalPlayerData? {
        Dao.getInstance().playerCache[uuid].also {
            return it
        }
    }

    data class InternalPlayerData(val playerUUID: String, var kitsCache: HashMap<String, Long>, var homeCache: HashMap<String, Location>, var FakeNick: String)
}