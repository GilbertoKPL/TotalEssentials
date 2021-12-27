package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PlayerData(player: String) {

    data class InternalPlayerData(
        val playerUUID: String,
        val coolDownCommand: HashMap<String, Long>,
        var kitsCache: HashMap<String, Long>,
        var homeCache: HashMap<String, Location>,
        var homeLimitCache: Int,
        var fakeNickCache: String,
        var gameModeCache: Int,
        var vanishCache: Boolean,
        var lightCache: Boolean,
        var flyCache: Boolean
    )

    private val p = EssentialsK.instance.server.getPlayerExact(player)

    private val online = p != null

    private val uuid = if (p != null) {
        PlayerUtil.getInstance().getPlayerUUID(p)
    } else {
        @Suppress("DEPRECATION")
        PlayerUtil.getInstance().getPlayerUUID(EssentialsK.instance.server.getOfflinePlayer(player))
    }

    fun getCache(): InternalPlayerData? {
        Dao.getInstance().playerCache[uuid].also {
            return it
        }
    }

    fun checkSql(): Boolean {
        return if (online) {
            true
        } else {
            try {
                var check = false
                transaction(SqlUtil.getInstance().sql) {
                    check = PlayerDataSQL.select { PlayerInfo eq uuid }.empty()
                }
                return !check
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return false
        }
    }

    private fun createEmptyCache(limitHome: Int): InternalPlayerData {
        return InternalPlayerData(
            uuid,
            HashMap(5),
            HashMap(40),
            HashMap(40),
            limitHome,
            "",
            0,
            vanishCache = false,
            lightCache = false,
            flyCache = false
        )
    }

    //death

    fun death() {
        val cache = getCache()!!
        startGamemodeCache(cache.gameModeCache)
        startVanishCache(cache.vanishCache)
        startLightCache(cache.lightCache)
        startFlyCache(cache.flyCache)
    }

    //coolDown

    fun setCoolDown(commandName: String, Long: Long) {
        val cache = getCache()!!
        cache.coolDownCommand[commandName] = Long
    }

    fun getCoolDown(commandName: String): Long {
        val cache = getCache()!!
        return cache.coolDownCommand[commandName] ?: 0
    }

    //Kits

    private fun startKitCache(timeKits: String): HashMap<String, Long> {
        val cacheKits = HashMap<String, Long>(40)
        for (kits in timeKits.split("|")) {
            try {
                if (kits != "") {
                    val split = kits.split(",")
                    val timeKit = split[1].toLong()
                    val nameKit = split[0]

                    val kitsCache = KitData(nameKit)

                    if (!kitsCache.checkCache()) {
                        val timeAll = KitData(nameKit).getCache(true)

                        if (timeKit != 0L && timeAll != null && (timeAll.time + timeKit) > System.currentTimeMillis()) {
                            cacheKits[nameKit] = timeKit
                            continue
                        }
                        continue
                    }
                }
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        return cacheKits
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
            var newPlace = ""
            for (i in kitTime.split("|")) {
                if (i.split(",")[0] != kit) {
                    newPlace += if (newPlace == "") {
                        i
                    } else {
                        "|$i"
                    }
                }
            }
            newPlace += if (newPlace == "") {
                "$kit,$time"
            } else {
                "|$kit,$time"
            }
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                    it[KitsTime] = newPlace
                }
            }
        }
    }

    fun delKitTime(kit: String) {
        if (online) {
            val cache = getCache()?.kitsCache ?: return
            cache.remove(kit)
            getCache()?.let { it.kitsCache = cache } ?: return
        }
    }

    //Home

    private fun startHomeCache(homesList: String): HashMap<String, Location> {
        val cacheHomes = HashMap<String, Location>(40)

        for (h in homesList.split("|")) {
            if (h == "") continue
            val split = h.split(",")
            val locationHome = LocationUtil.getInstance().locationSerializer(split[1]) ?: continue
            val nameHome = split[0]
            cacheHomes[nameHome] = locationHome
        }

        return cacheHomes
    }

    fun setHome(name: String, loc: Location) {
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
                return emptyList()
            }
            for (h in homesList.split("|")) {
                if (h == "") continue
                val split = h.split(",")
                val nameHome = split[0]
                cacheHomes.add(nameHome)
            }
            cacheHomes
        }
    }

    fun getHomeLocation(home: String): Location? {
        return if (online) {
            val cache = getCache() ?: return null
            cache.homeCache[home.lowercase()]
        } else {
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
                    return LocationUtil.getInstance().locationSerializer(split[1])
                }
            }
            null
        }
    }

    //Nick

    fun checkNick(): Boolean {
        val cache = getCache()
        return online && cache != null && cache.fakeNickCache != ""
    }

    private fun startNickCache(fakeNick: String): String {
        if (fakeNick != "") {
            p!!.setDisplayName(fakeNick)
            p.setPlayerListName(fakeNick)
        }
        return fakeNick
    }

    fun setNick(newNick: String, other: Boolean = false): Boolean {
        if (online && !other) {
            var exist = false
            transaction(SqlUtil.getInstance().sql) {
                exist = PlayerDataSQL.select { FakeNick eq newNick }.empty()
            }
            if (!MainConfig.getInstance().nicksCanPlayerHaveSameNick) {
                if (!exist) {
                    return true
                }
            }
            p!!.setDisplayName(newNick)
            p.setPlayerListName(newNick)
        }
        if (online && other) {
            p!!.setDisplayName(newNick)
            p.setPlayerListName(newNick)
            getCache()?.let { it.fakeNickCache = newNick } ?: return true
        }
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.update({ PlayerInfo eq uuid }) {
                it[FakeNick] = newNick
            }
        }
        return false
    }

    fun delNick() {
        //cache
        if (online) {
            getCache()?.let { it.fakeNickCache = "" } ?: return
            p!!.setDisplayName(p.name)
            p.setPlayerListName(p.name)
        }

        //sql
        SqlUtil.getInstance().helperUpdater(uuid, FakeNick, "")
    }

    //gamemode

    private fun startGamemodeCache(gameMode: Int): Int {
        val gameModeName = PlayerUtil.getInstance().getGamemodeNumber(gameMode.toString())

        if (p?.gameMode != gameModeName) {
            p?.gameMode = gameModeName
        }

        return gameMode
    }

    fun getGamemode() :  Int {
        if (online) {
            val cache = getCache() ?: return 0
            return cache.gameModeCache
        }
        return 0
    }

    fun setGamemode(gm: GameMode, value: Int) {
        if (online) {
            p!!.gameMode = gm
            val cache = getCache() ?: return
            cache.gameModeCache = value

            //desbug fly on set gamemode 0
            if (gm == GameMode.SURVIVAL && checkFly()) {
                p.allowFlight = true
                p.isFlying = true
            }

            //sql
            SqlUtil.getInstance().helperUpdater(uuid, PlayerDataSQL.GameMode, value)
        }
    }

    //vanish

    private fun startVanishCache(active: Boolean): Boolean {
        if (active) {
            p!!.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            for (it in ReflectUtil.getInstance().getPlayers()) {
                if (it.player!!.hasPermission("essentialsk.commands.vanish")
                    || it.player!!.hasPermission("essentialsk.bypass.vanish")
                ) {
                    continue
                }
                @Suppress("DEPRECATION")
                it.hidePlayer(p)
            }
        }
        return active
    }

    fun checkVanish(): Boolean {
        val cache = getCache()
        return if (online && cache != null) {
            cache.vanishCache
        } else {
            false
        }
    }

    //only online
    fun switchVanish(): Boolean {

        val cache = getCache()

        if (online && cache != null) {

            val newValue = cache.vanishCache.not()

            cache.vanishCache = newValue

            //sql
            SqlUtil.getInstance().helperUpdater(uuid, PlayerDataSQL.Vanish, newValue)

            return if (newValue) {
                p!!.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
                ReflectUtil.getInstance().getPlayers().forEach {
                    @Suppress("DEPRECATION")
                    if (!it.hasPermission("essentialsk.commands.vanish") && !it.hasPermission("essentialsk.bypass.vanish")) {
                        it.hidePlayer(p)
                    }
                }
                true
            } else {
                p!!.removePotionEffect(PotionEffectType.INVISIBILITY)
                ReflectUtil.getInstance().getPlayers().forEach {
                    @Suppress("DEPRECATION")
                    it.showPlayer(p)
                }
                false
            }
        }
        return false
    }

    //light

    private fun startLightCache(active: Boolean): Boolean {
        if (active) {
            p!!.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
        }
        return active
    }

    fun switchLight(): Boolean {

        val cache = getCache()

        if (online && cache != null) {

            val newValue = cache.lightCache.not()

            cache.lightCache = newValue

            //sql
            SqlUtil.getInstance().helperUpdater(uuid, PlayerDataSQL.Light, newValue)

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

    //Fly

    private fun startFlyCache(active: Boolean): Boolean {
        if (active) {
            p!!.allowFlight = true
            p.isFlying = true
        }
        return active
    }

    fun checkFly(): Boolean {
        val cache = getCache()
        return if (online && cache != null) {
            cache.flyCache
        } else {
            false
        }
    }

    fun switchFly(): Boolean {

        val cache = getCache()

        if (online && cache != null) {

            val newValue = cache.flyCache.not()

            cache.flyCache = newValue

            //sql
            SqlUtil.getInstance().helperUpdater(uuid, PlayerDataSQL.Vanish, newValue)

            return if (newValue) {
                p!!.allowFlight = true
                p.isFlying = true
                true
            } else {
                //desbug gamemode
                if (cache.gameModeCache != 1 && cache.gameModeCache != 3) {
                    p!!.allowFlight = false
                    p.isFlying = false
                }
                false
            }
        }
        return false
    }

    //load

    fun loadCache() {
        val limitHome: Int = PermissionUtil.getInstance().getNumberPermission(
            p!!,
            "essentialsk.commands.sethome.",
            MainConfig.getInstance().homesDefaultLimitHomes
        )
        if (Dao.getInstance().playerCache.containsKey(uuid)) {
            val cache = getCache()!!
            cache.homeLimitCache = limitHome
            startNickCache(cache.fakeNickCache)
            startGamemodeCache(cache.gameModeCache)
            startVanishCache(cache.vanishCache)
            startLightCache(cache.lightCache)
            startFlyCache(cache.flyCache)

            if (!checkVanish()) {
                if (MainConfig.getInstance().messagesLoginMessage) {
                    PluginUtil.getInstance().serverMessage(
                        GeneralLang.getInstance().messagesEnterMessage
                            .replace("%player%", p.name)
                    )
                }
                if (MainConfig.getInstance().discordbotSendLoginMessage) {
                    PlayerUtil.getInstance().sendLoginEmbed(p)
                }
            }
            return
        }
        //values
        var timeKits = ""
        var homesList = ""
        var fakeNick = ""
        var gameMode = 0
        var vanish = false
        var light = false
        var fly = false

        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerInfo eq uuid }.also { query ->
                    if (query.empty()) {
                        PlayerDataSQL.insert {
                            it[PlayerInfo] = this@PlayerData.uuid
                        }
                        Dao.getInstance().playerCache[uuid] = createEmptyCache(limitHome)
                        return@transaction
                    }
                    timeKits = query.single()[KitsTime]
                    homesList = query.single()[SavedHomes]
                    fakeNick = query.single()[FakeNick]
                    gameMode = query.single()[PlayerDataSQL.GameMode]
                    vanish = query.single()[PlayerDataSQL.Vanish]
                    light = query.single()[PlayerDataSQL.Light]
                    fly = query.single()[PlayerDataSQL.Fly]
                }
            }

            EssentialsK.instance.server.scheduler.runTask(EssentialsK.instance, Runnable {
                Dao.getInstance().playerCache[uuid] = InternalPlayerData(
                    uuid,
                    HashMap(5),
                    kitsCache = startKitCache(timeKits),
                    homeCache = startHomeCache(homesList),
                    homeLimitCache = limitHome,
                    fakeNickCache = startNickCache(fakeNick),
                    gameModeCache = startGamemodeCache(gameMode),
                    vanishCache = startVanishCache(vanish),
                    lightCache = startLightCache(light),
                    flyCache = startFlyCache(fly)
                )

                if (!checkVanish()) {
                    if (MainConfig.getInstance().messagesLoginMessage) {
                        PluginUtil.getInstance().serverMessage(
                            GeneralLang.getInstance().messagesEnterMessage
                                .replace("%player%", p.name)
                        )
                    }
                    if (MainConfig.getInstance().discordbotSendLoginMessage) {
                        PlayerUtil.getInstance().sendLoginEmbed(p)
                    }
                }
            })
        }
    }
}