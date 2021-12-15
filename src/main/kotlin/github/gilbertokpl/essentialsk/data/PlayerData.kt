package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.KitsTime
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.PlayerInfo
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.SavedHomes
import github.gilbertokpl.essentialsk.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

@Suppress("DEPRECATION")
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
            var timeKits = ""
            var fakeNick = ""
            var homesList = ""
            var gameMode = 0
            var vanish = false
            var light = false

            //internal
            var emptyQuery = false

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerInfo eq uuid }.also { query ->
                    emptyQuery = query.empty()
                    if (emptyQuery) {
                        PlayerDataSQL.insert {
                            it[PlayerInfo] = this@PlayerData.uuid
                        }
                        return@transaction
                    }
                    timeKits = query.single()[KitsTime]
                    fakeNick = query.single()[FakeNick]
                    homesList = query.single()[SavedHomes]
                    gameMode = query.single()[PlayerDataSQL.GameMode]
                    vanish = query.single()[PlayerDataSQL.Vanish]
                    light = query.single()[PlayerDataSQL.Light]
                }
            }

            val limitHome: Int =
                PluginUtil.getInstance().getNumberPermission(
                    p!!,
                    "essentialsk.commands.sethome.",
                    MainConfig.getInstance().homesDefaultLimitHomes
                )

            if (emptyQuery) {
                Dao.getInstance().playerCache[uuid] = createEmptyCache(limitHome)
                return@asyncExecutor
            }

            //other values
            val cacheKits = HashMap<String, Long>(40)
            val cacheHomes = HashMap<String, Location>(40)
            var replace = ""
            var update = false

            for (kits in timeKits.split("|")) {
                try {
                    if (kits != "") {
                        val split = kits.split(".")
                        val timeKit = split[1].toLong()
                        val nameKit = split[0]

                        val kitsCache = KitData(nameKit)

                        if (kitsCache.checkCache()) {
                            update = true
                        } else {
                            val timeAll = KitData(nameKit).getCache(true)

                            if (timeKit != 0L && timeAll != null && (timeAll.time + timeKit) > System.currentTimeMillis()) {
                                replace += if (replace == "") {
                                    "$nameKit,$timeKit"
                                } else {
                                    "|$nameKit,$timeKit"
                                }
                                cacheKits[nameKit] = timeKit
                                continue
                            }
                        }
                    }
                } catch (ignored: Exception) {
                    update = true
                }
            }

            //nick
            if (fakeNick != "") {
                p.setDisplayName(fakeNick)
            }

            //gamemode
            val gameModeName: GameMode = when (gameMode) {
                0 -> GameMode.SURVIVAL
                1 -> GameMode.CREATIVE
                2 -> try {
                    GameMode.ADVENTURE
                } catch (e: Exception) {
                    GameMode.SURVIVAL
                }
                3 -> try {
                    GameMode.SPECTATOR
                } catch (e: Exception) {
                    GameMode.SURVIVAL
                }
                else -> GameMode.SURVIVAL
            }

            EssentialsK.instance.server.scheduler.runTask(EssentialsK.instance, Runnable {
                //gamemode
                if (p.gameMode != gameModeName) {
                    p.gameMode = gameModeName
                }

                //vanish
                if (vanish) {
                    p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
                    for (it in ReflectUtil.getInstance().getPlayers()) {
                        if (it.player!!.hasPermission("essentialsk.commands.vanish")
                            || it.player!!.hasPermission("essentialsk.bypass.vanish")
                        ) {
                            continue
                        }
                        it.hidePlayer(p)
                    }
                }
                //light
                if (light) {
                    p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
                }
            })

            //home
            for (h in homesList.split("|")) {
                if (h == "") continue
                val split = h.split(",")
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
                fakeNick,
                gameMode,
                vanish,
                light
            )

            if (update) {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update {
                        it[KitsTime] = replace
                    }
                }
            }
        }
    }

    private fun createEmptyCache(limitHome: Int): InternalPlayerData {
        return InternalPlayerData(
            uuid,
            HashMap(40),
            HashMap(40),
            limitHome,
            "",
            0,
            vanish = false,
            light = false
        )
    }

    fun checkSql(): Boolean {
        return if (online) {
            true
        } else {
            CompletableFuture.supplyAsync({
                try {
                    var check = false
                    transaction(SqlUtil.getInstance().sql) {
                        check = PlayerDataSQL.select { PlayerInfo eq uuid }.empty()
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
    fun setGamemode(gm: GameMode, value: Int) {
        if (online) {
            p!!.gameMode = gm
            val cache = getCache() ?: return
            cache.gameMode = value

            //sql
            helperUpdater(PlayerDataSQL.GameMode, value)
        }
    }

    fun checkVanish(): Boolean {
        val cache = getCache()
        return if (online && cache != null) {
            cache.vanish
        } else {
            false
        }
    }

    //only online
    fun switchVanish(): Boolean {

        val cache = getCache()

        if (online && cache != null) {

            val newValue = cache.vanish.not()

            cache.vanish = newValue

            //sql
            helperUpdater(PlayerDataSQL.Vanish, newValue)

            return if (newValue) {
                p!!.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
                ReflectUtil.getInstance().getPlayers().forEach {
                    it.hidePlayer(p)
                }
                true
            } else {
                p!!.removePotionEffect(PotionEffectType.INVISIBILITY)
                ReflectUtil.getInstance().getPlayers().forEach {
                    it.showPlayer(p)
                }
                false
            }
        }
        return false
    }

    //only online
    fun switchLight(): Boolean {

        val cache = getCache()

        if (online && cache != null) {

            val newValue = cache.light.not()

            cache.light = newValue

            //sql
            helperUpdater(PlayerDataSQL.Light, newValue)

            return if (newValue) {
                p!!.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
                true
            } else {
                p!!.removePotionEffect(PotionEffectType.NIGHT_VISION)
                false
            }
        }
        return false
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
                PlayerDataSQL.select { PlayerInfo eq uuid }.also {
                    query = it.empty()
                    kitTime = it.single()[KitsTime]
                }
            }

            if (query || kitTime == "") {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                        it[KitsTime] = "$kit,$time"
                    }
                }
                return@asyncExecutor
            }
            val check = kitTime.split("|")
            var newPlace = ""
            for (i in check) {
                if (i.split(",")[0] != kit) {
                    newPlace += if (newPlace == "") {
                        i
                    } else {
                        "|$i"
                    }
                }
            }
            newPlace += if (newPlace == "") {
                "$kit,${System.currentTimeMillis()}"
            } else {
                "-$kit,$time"
            }
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                    it[KitsTime] = newPlace
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
                PlayerDataSQL.select { PlayerInfo eq uuid }.also { query ->
                    emptyQuery = query.empty()
                    if (emptyQuery) {
                        PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                            it[SavedHomes] = "$name,$serializedHome"
                        }
                        return@transaction
                    }
                    homes = query.single()[SavedHomes]
                }
            }
            if (emptyQuery) return@asyncExecutor

            var newHome = "$name,$serializedHome"

            for (h in homes.split("|")) {
                if (h == "") continue
                newHome += "|$h"
            }

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                    it[SavedHomes] = newHome
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
                PlayerDataSQL.select { PlayerInfo eq uuid }.also { query ->
                    homes = query.single()[SavedHomes]
                }
            }

            var newHome = ""

            for (h in homes.split("|")) {
                if (h.split(",")[0] == name) continue
                newHome += if (newHome == "") {
                    h
                } else {
                    "|$h"
                }
            }

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                    it[SavedHomes] = newHome
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
                    PlayerDataSQL.select { PlayerInfo eq uuid }.also { query ->
                        if (query.empty()) {
                            bol = true
                            return@transaction
                        }
                        homesList = query.single()[SavedHomes]
                    }
                }
                if (bol) {
                    return@supplyAsync emptyList()
                }
                for (h in homesList.split("|")) {
                    if (h == "") continue
                    val split = h.split(",")
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
                        PlayerDataSQL.select { PlayerInfo eq uuid }.also { query ->
                            homesList = query.single()[SavedHomes]
                        }
                    }
                    for (h in homesList.split("|")) {
                        if (h == "") continue
                        val split = h.split(",")
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
                    var exist = false
                    transaction(SqlUtil.getInstance().sql) {
                        exist = PlayerDataSQL.select { FakeNick eq newNick }.empty()
                    }
                    if (!MainConfig.getInstance().nicksCanPlayerHaveSameNick) {
                        if (!exist) {
                            return@supplyAsync true
                        }
                    }
                }
                if (online && other) {
                    p!!.setDisplayName(newNick)
                    getCache()?.let { it.fakeNick = newNick } ?: return@supplyAsync true
                }
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update({ PlayerInfo eq uuid }) {
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
            getCache()?.let { it.fakeNick = "" } ?: return
            p!!.setDisplayName(p.name)
        }

        //sql
        helperUpdater(FakeNick, "")
    }

    private fun <T> helperUpdater(col: Column<T>, value: T) {
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                    it[col] = value
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
        var fakeNick: String,
        var gameMode: Int,
        var vanish: Boolean,
        var light: Boolean
    )
}