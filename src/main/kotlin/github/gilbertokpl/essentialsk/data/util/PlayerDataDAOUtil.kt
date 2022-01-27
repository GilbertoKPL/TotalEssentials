package github.gilbertokpl.essentialsk.data.util

import github.gilbertokpl.essentialsk.data.dao.KitDataDAO
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.playerTable
import github.gilbertokpl.essentialsk.util.*
import github.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

internal object PlayerDataDAOUtil {

    fun createEmptyCache(p: Player, playerID: String, limitHome: Int): PlayerDataDAO {
        return PlayerDataDAO(
            playerID,
            p,
            HashMap(5),
            HashMap(40),
            HashMap(40),
            limitHome,
            "",
            0,
            vanishCache = false,
            lightCache = false,
            flyCache = false,
            backLocation = p.location,
            speedCache = 1,
            inTeleport = false
        )
    }

    //death
    fun death(p: Player) {
        val cache = PlayerDataDAO[p] ?: return
        startGamemodeCache(p, cache.gameModeCache)
        startVanishCache(p, cache.vanishCache)
        startLightCache(p, cache.lightCache)
        startFlyCache(p, cache.flyCache)
    }

    //kits
    fun startKitCache(timeKits: String): HashMap<String, Long> {
        val cacheKits = HashMap<String, Long>(40)

        for (kits in timeKits.split("|")) {
            try {
                if (kits != "") {
                    val split = kits.split(",")
                    val timeKit = split[1].toLong()
                    val nameKit = split[0]

                    val kitsCache = KitDataDAO[nameKit]

                    if (kitsCache != null) {
                        val timeAll = kitsCache.time

                        if ((timeKit != 0L) && ((timeAll + timeKit) > System.currentTimeMillis())) {
                            cacheKits[nameKit] = timeKit
                        }
                        continue
                    }
                }
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        return cacheKits
    }

    //home
    fun startHomeCache(homesList: String): HashMap<String, Location> {
        val cacheHomes = HashMap<String, Location>(40)

        for (h in homesList.split("|")) {
            if (h == "") continue
            val split = h.split(",")
            val locationHome = LocationUtil.locationSerializer(split[1]) ?: continue
            val nameHome = split[0]
            cacheHomes[nameHome] = locationHome
        }

        return cacheHomes
    }

    //nick
    fun startNickCache(p: Player, fakeNick: String): String {
        if (fakeNick != "") {
            p.setDisplayName(fakeNick)
        }
        return fakeNick
    }

    //gamemode
    fun startGamemodeCache(p: Player, gameMode: Int): Int {
        val gameModeName = PlayerUtil.getGamemodeNumber(gameMode.toString())

        if (p.gameMode != gameModeName) {
            p.gameMode = gameModeName
        }

        return gameMode
    }

    //vanish
    fun startVanishCache(p: Player, active: Boolean): Boolean {
        if (active) {
            p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            for (it in ReflectUtil.getPlayers()) {
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

    //light
    fun startLightCache(p: Player, active: Boolean): Boolean {
        if (active) {
            p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
        }
        return active
    }

    //fly
    fun startFlyCache(p: Player, active: Boolean): Boolean {
        if (active) {
            p.allowFlight = true
            p.isFlying = true
        }
        return active
    }

    //back
    fun startBackCache(loc: String): Location? {
        return LocationUtil.locationSerializer(loc)
    }

    //speed
    fun startSpeedCache(p: Player, speed: Int): Int {
        if (speed == 1) {
            return speed
        }
        p.walkSpeed = (speed * 0.1).toFloat()
        p.flySpeed = (speed * 0.1).toFloat()
        return speed
    }

    //load

    fun saveAllCache() {
        for (i in PlayerDataDAO.getValues()) {
            saveCache(i.playerID)
        }
    }

    fun saveCache(p: Player) {
        val playerID = PlayerUtil.getPlayerUUID(p)
        CoroutineScope(BukkitDispatcher(async = true)).launch {
            saveCache(playerID)
        }
    }

    private fun saveCache(playerID: String) {
        val cache = PlayerDataDAO[playerID] ?: return
        val backLocation = LocationUtil.locationSerializer(cache.backLocation)

        transaction(SqlUtil.sql) {
            PlayerDataSQL.update({ playerTable eq playerID }) {
                it[gameModeTable] = cache.gameModeCache
                it[vanishTable] = cache.vanishCache
                it[lightTable] = cache.lightCache
                it[flyTable] = cache.flyCache
                it[backTable] = backLocation
            }
        }
    }
}
