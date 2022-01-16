package github.gilbertokpl.essentialsk.data.start

import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.PlayerInfo
import github.gilbertokpl.essentialsk.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object PlayerDataLoader {

    fun createEmptyCache(p: Player, playerID: String, limitHome: Int): PlayerDataV2 {
        return PlayerDataV2(
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
            speedCache = 1
        )
    }

    //death
    fun death(p: Player) {
        val cache = PlayerDataV2[p] ?: return
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

                    val kitsCache = KitDataV2[nameKit]

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

    fun saveCache(p: Player) {
        TaskUtil.asyncExecutor {
            transaction(SqlUtil.sql) {
                val playerID = PlayerUtil.getPlayerUUID(p)
                val cache = PlayerDataV2[p] ?: return@transaction
                PlayerDataSQL.update({ PlayerInfo eq playerID }) {
                    it[GameMode] = cache.gameModeCache
                    it[Vanish] = cache.vanishCache
                    it[Light] = cache.lightCache
                    it[Fly] = cache.flyCache
                    it[Back] =
                        cache.backLocation?.let { it1 -> LocationUtil.locationSerializer(it1) } ?: ""
                }
            }
        }
    }
}
