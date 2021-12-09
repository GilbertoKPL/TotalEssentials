package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.kitsTime
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.savedHomes
import github.gilbertokpl.essentialsk.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture


class PlayerData(player: String) {

    private val p = EssentialsK.instance.server.getPlayer(player)

    private val online = p != null

    private val uuid = if (p != null) {
        PluginUtil.getInstance().getPlayerUUID(p)
    } else {
        CompletableFuture.supplyAsync({
            PluginUtil.getInstance().getPlayerUUID(EssentialsK.instance.server.getOfflinePlayer(player))
        }, TaskUtil.getInstance().getExecutor()).get()
    }

    fun unloadCache() {
        Dao.getInstance().playerCache.remove(uuid)
    }

    fun loadCache() {
        TaskUtil.getInstance().asyncExecutor {
            //values
            val cacheKits = HashMap<String, Long>(40)
            val cacheHomes = HashMap<String, Location>(40)
            val limitHome: Int =
                PluginUtil.getInstance().getNumberPermission(
                    p!!,
                    "essentialsk.commands.sethome.",
                    MainConfig.getInstance().homesDefaultLimitHomes
                )
            var fakeNick = ""

            //internal
            lateinit var homesList: String
            lateinit var timeKits: String
            var emptyQuery = false

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    emptyQuery = query.empty()
                    if (emptyQuery) {
                        PlayerDataSQL.insert {
                            it[uuid] = this@PlayerData.uuid
                            it[kitsTime] = ""
                            it[savedHomes] = ""
                            it[FakeNick] = ""
                        }
                        return@transaction
                    }
                    timeKits = query.single()[kitsTime]
                    fakeNick = query.single()[FakeNick]
                    homesList = query.single()[savedHomes]
                }
            }

            if (emptyQuery) {
                Dao.getInstance().playerCache[uuid] = InternalPlayerData(
                    uuid,
                    HashMap(40),
                    HashMap(40),
                    limitHome,
                    fakeNick
                )
                return@asyncExecutor
            }

            var replace = ""
            var update = false

            for (kits in timeKits.split("|")) {
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
                        replace += if (replace == "") {
                            "$nameKit.$timeKit"
                        } else {
                            "|$nameKit.$timeKit"
                        }
                        cacheKits[nameKit] = timeKit
                        continue
                    }
                } catch (ignored: Exception) {
                    update = true
                }
            }

            //nick
            if (fakeNick != "") {
                p.setDisplayName(fakeNick)
            }

            //home
            for (h in homesList.split("|")) {
                if (h == "") continue
                val split = h.split(".")
                val locationHome = LocationUtil.getInstance().locationSerializer(split[1])
                val nameHome = split[0]
                cacheHomes[nameHome] = locationHome
            }

            //cache is in final to improve protection to load caches
            Dao.getInstance().playerCache[uuid] = InternalPlayerData(
                uuid,
                cacheKits,
                cacheHomes,
                limitHome,
                fakeNick
            )

            if (update) {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update {
                        it[kitsTime] = replace
                    }
                }
            }
        }
    }

    fun checkSql(): Boolean {
        return if (online) {
            true
        } else {
            CompletableFuture.supplyAsync({
                try {
                    var check = false
                    transaction(SqlUtil.getInstance().sql) {
                        check = PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.empty()
                    }
                    return@supplyAsync !check
                } catch (ex: Exception) {
                    FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                }
                return@supplyAsync false
            }, TaskUtil.getInstance().getExecutor()).get()
        }
    }

    //only online
    fun delKitTime(kit: String) {
        if (online) {
            val cache = getCache()?.kitsCache ?: return
            cache.remove(kit)
            getCache()?.let { it.kitsCache = cache } ?: return
        }
    }

    fun setKitTime(kit: String, time: Long) {
        //cache
        if (online) {
            val cache = getCache()?.kitsCache ?: return
            cache.remove(kit)
            cache[kit] = time
            getCache()?.let { it.kitsCache = cache } ?: return
        }

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
            val check = kitTime.split("|")
            var newPlace = ""
            for (i in check) {
                if (i.split(".")[0] != kit) {
                    if (newPlace == "") {
                        newPlace += i
                        continue
                    }
                    newPlace += "|$i"
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

    fun createHome(name: String, loc: Location) {
        //cache
        if (online) {
            val cache = getCache()?.homeCache ?: return
            cache[name] = loc
            getCache()?.let { it.homeCache = cache } ?: return
        }

        //sql
        TaskUtil.getInstance().asyncExecutor {
            lateinit var homes: String
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

    fun delHome(name: String) {
        //cache
        if (online) {
            val cache = getCache()?.homeCache ?: return
            cache.remove(name)
            getCache()?.let { it.homeCache = cache } ?: return
        }

        //sql
        TaskUtil.getInstance().asyncExecutor {
            lateinit var homes: String
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    homes = query.single()[savedHomes]
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

    fun getHomeList(): List<String> {
        return if (online) {
            getCache()?.let { get -> get.homeCache.map { it.key } } ?: return emptyList()
        } else {
            CompletableFuture.supplyAsync({
                lateinit var homesList: String
                val cacheHomes = ArrayList<String>()
                var bol = false
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                        if (query.empty()) {
                            bol = true
                            return@transaction
                        }
                        homesList = query.single()[savedHomes]
                    }
                }
                if (bol) {
                    return@supplyAsync emptyList()
                }
                for (h in homesList.split("|")) {
                    if (h == "") continue
                    val split = h.split(".")
                    val nameHome = split[0]
                    cacheHomes.add(nameHome)
                }
                return@supplyAsync cacheHomes
            }, TaskUtil.getInstance().getExecutor()).get()
        }
    }

    fun getLocationOfHome(home: String): Location? {
        return if (online) {
            val cache = getCache() ?: return null
            cache.homeCache[home.lowercase()]
        } else {
            CompletableFuture.supplyAsync({
                try {
                    lateinit var homesList: String
                    transaction(SqlUtil.getInstance().sql) {
                        PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                            homesList = query.single()[savedHomes]
                        }
                    }
                    for (h in homesList.split("|")) {
                        if (h == "") continue
                        val split = h.split(".")
                        if (home.lowercase() == split[0]) {
                            return@supplyAsync LocationUtil.getInstance().locationSerializer(split[1])
                        }
                    }
                } catch (ex: Exception) {
                    FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                }
                return@supplyAsync null
            }, TaskUtil.getInstance().getExecutor()).get()
        }
    }

    fun setNick(newNick: String, other: Boolean = false): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({
            try {
                if (online && !other) {
                    lateinit var players: List<String>
                    transaction(SqlUtil.getInstance().sql) {
                        players = PlayerDataSQL.selectAll().map { it[FakeNick] }
                    }
                    if (!MainConfig.getInstance().nicksCanPlayerHaveSameNick) {
                        for (i in players) {
                            if (i.equals(newNick, true)) {
                                return@supplyAsync true
                            }
                        }
                    }
                }
                if (online && other) {
                    p!!.setDisplayName(newNick)
                    getCache()?.let { it.FakeNick = newNick } ?: return@supplyAsync true
                }
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                        it[FakeNick] = newNick
                    }
                }
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return@supplyAsync false
        }, TaskUtil.getInstance().getExecutor())
    }

    fun removeNick() {
        //cache
        if (online) {
            getCache()?.let { it.FakeNick = "" } ?: return
            p!!.setDisplayName(p.name)
        }

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

    data class InternalPlayerData(
        val playerUUID: String,
        var kitsCache: HashMap<String, Long>,
        var homeCache: HashMap<String, Location>,
        var homeLimit: Int,
        var FakeNick: String
    )
}