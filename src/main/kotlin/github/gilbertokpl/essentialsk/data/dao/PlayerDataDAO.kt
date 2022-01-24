package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.util.PlayerDataSQLUtil
import github.gilbertokpl.essentialsk.data.util.PlayerDataDAOUtil
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.backTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.flyTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.gameModeTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.homeTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.kitsTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.lightTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.nickTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.speedTable
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL.vanishTable
import github.gilbertokpl.essentialsk.util.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

internal data class PlayerDataDAO(
    val playerID: String,
    var player: Player,
    val coolDownCommand: HashMap<String, Long>,
    var kitsCache: HashMap<String, Long>,
    var homeCache: HashMap<String, Location>,
    var homeLimitCache: Int,
    var fakeNickCache: String,
    var gameModeCache: Int,
    var vanishCache: Boolean,
    var lightCache: Boolean,
    var flyCache: Boolean,
    var backLocation: Location?,
    var speedCache: Int,
    var inTeleport: Boolean
) {
    //coolDown

    fun setCoolDown(commandName: String, Long: Long) {
        coolDownCommand[commandName] = Long
    }

    fun getCoolDown(commandName: String): Long {
        return coolDownCommand[commandName] ?: 0
    }

    fun setKitTime(kit: String, time: Long) {
        kitsCache[kit] = time

        TaskUtil.asyncExecutor {
            PlayerDataSQLUtil.setKitTimeSQL(kit, time, playerID)
        }
    }

    fun delKitTime(kit: String) {
        kitsCache.remove(kit)
    }

    fun setHome(name: String, loc: Location) {
        homeCache[name] = loc

        TaskUtil.asyncExecutor {
            PlayerDataSQLUtil.setHomeSQL(name, loc, playerID)
        }
    }

    fun delHome(name: String) {
        homeCache.remove(name)

        TaskUtil.asyncExecutor {
            PlayerDataSQLUtil.delHomeSQL(name, playerID)
        }
    }

    fun getHomeList(): List<String> {
        return homeCache.map { it.key }
    }

    fun getHomeLocation(home: String): Location? {
        return homeCache[home.lowercase()]
    }

    //Nick

    fun setNick(newNick: String, other: Boolean = false): Boolean {
        if (!other) {
            var exist = false
            transaction(SqlUtil.sql) {
                exist = PlayerDataSQL.select { nickTable eq newNick }.empty()
            }
            if (!MainConfig.nicksCanPlayerHaveSameNick &&
                !player.hasPermission("essentialsk.bypass.nickblockednicks") &&
                !exist
            ) {
                return true
            }
            player.setDisplayName(newNick)
        }
        if (other) {
            player.setDisplayName(newNick)
            fakeNickCache = newNick
        }
        //sql
        SqlUtil.helperUpdater(playerID, nickTable, newNick)
        return false
    }

    fun delNick() {
        //cache
        fakeNickCache = ""

        player.setDisplayName(player.name)

        //sql
        SqlUtil.helperUpdater(playerID, nickTable, "")
    }

    //gamemode

    fun setGamemode(gm: GameMode) {
        player.gameMode = gm

        gameModeCache = PlayerUtil.getNumberGamemode(gm)

        //desbug fly on set gamemode 0
        if (gm == GameMode.SURVIVAL && flyCache) {
            player.allowFlight = true
            player.isFlying = true
        }

    }


    //vanish

    fun switchVanish(): Boolean {

        val newValue = vanishCache.not()

        vanishCache = newValue

        return if (newValue) {
            player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            ReflectUtil.getPlayers().forEach {
                @Suppress("DEPRECATION")
                if (!it.hasPermission("essentialsk.commands.vanish") &&
                    !it.hasPermission("essentialsk.bypass.vanish")
                ) {
                    it.hidePlayer(player)
                }
            }
            true
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            ReflectUtil.getPlayers().forEach {
                @Suppress("DEPRECATION")
                it.showPlayer(player)
            }
            false
        }
    }

    //light

    fun switchLight(): Boolean {

        val newValue = lightCache.not()

        lightCache = newValue

        return if (newValue) {
            player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
            true
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION)
            false
        }
    }

    //fly

    fun switchFly(): Boolean {
        val newValue = flyCache.not()

        flyCache = newValue

        return if (newValue) {
            player.allowFlight = true
            player.isFlying = true
            true
        } else {
            //desbug gamemode
            if (gameModeCache != 1 && gameModeCache != 3) {
                player.allowFlight = false
                player.isFlying = false
            }
            false
        }
    }

    //back

    fun setBack(loc: Location) {
        backLocation = loc
    }

    fun clearBack() {
        backLocation = null
    }

    //speed

    fun setSpeed(vel: Int) {
        player.walkSpeed = (vel * 0.1).toFloat()
        player.flySpeed = (vel * 0.1).toFloat()
        speedCache = vel
    }

    fun clearSpeed() {
        player.walkSpeed = 0.2F
        player.flySpeed = 0.1F
        speedCache = 1
    }

    companion object : Listener {

        private val playerCacheV2 = HashMap<String, PlayerDataDAO>()

        operator fun get(p: Player) = playerCacheV2[PlayerUtil.getPlayerUUID(p)]

        operator fun get(playerID: String) = playerCacheV2[playerID]

        fun getValues() = playerCacheV2.values

        fun loadCache(e: PlayerJoinEvent): Boolean {
            val p = e.player

            val limitHome: Int = PermissionUtil.getNumberPermission(
                p,
                "essentialsk.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )

            val cache = get(p) ?: run {
                val playerID = PlayerUtil.getPlayerUUID(p)
                //values
                var kitsList = ""
                var homesList = ""
                var nick = ""
                var gameMode = 0
                var vanish = false
                var light = false
                var fly = false
                var location = ""
                var speed = 1

                TaskUtil.asyncExecutor {
                    transaction(SqlUtil.sql) {
                        val query = PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }

                        if (query.empty()) {
                            PlayerDataSQL.insert {
                                it[playerTable] = playerID
                            }
                            playerCacheV2[p.name.lowercase()] =
                                PlayerDataDAOUtil.createEmptyCache(p, playerID, limitHome)
                            return@transaction
                        }

                        val single = query.single()
                        kitsList = single[kitsTable]
                        homesList = single[homeTable]
                        nick = single[nickTable]
                        gameMode = single[gameModeTable]
                        vanish = single[vanishTable]
                        light = single[lightTable]
                        fly = single[flyTable]
                        location = single[backTable]
                        speed = single[speedTable]
                    }

                    EssentialsK.instance.server.scheduler.runTaskLater(EssentialsK.instance, Runnable {

                        playerCacheV2[p.name.lowercase()] = PlayerDataDAO(
                            playerID = playerID,
                            player = p,
                            coolDownCommand = HashMap(5),
                            kitsCache = PlayerDataDAOUtil.startKitCache(kitsList),
                            homeCache = PlayerDataDAOUtil.startHomeCache(homesList),
                            homeLimitCache = limitHome,
                            fakeNickCache = PlayerDataDAOUtil.startNickCache(p, nick),
                            gameModeCache = PlayerDataDAOUtil.startGamemodeCache(p, gameMode),
                            vanishCache = PlayerDataDAOUtil.startVanishCache(p, vanish),
                            lightCache = PlayerDataDAOUtil.startLightCache(p, light),
                            flyCache = PlayerDataDAOUtil.startFlyCache(p, fly),
                            backLocation = PlayerDataDAOUtil.startBackCache(location),
                            speedCache = PlayerDataDAOUtil.startSpeedCache(p, speed),
                            inTeleport = false
                        )

                        PlayerUtil.finishLogin(p, vanish)

                    }, 5L)
                }
                return false
            }

            cache.homeLimitCache = limitHome
            cache.player = p

            EssentialsK.instance.server.scheduler.runTaskLater(
                EssentialsK.instance, Runnable
                {
                    PlayerUtil.finishLogin(p, cache.vanishCache)
                    PlayerDataDAOUtil.startNickCache(p, cache.fakeNickCache)
                    PlayerDataDAOUtil.startGamemodeCache(p, cache.gameModeCache)
                    PlayerDataDAOUtil.startVanishCache(p, cache.vanishCache)
                    PlayerDataDAOUtil.startLightCache(p, cache.lightCache)
                    PlayerDataDAOUtil.startFlyCache(p, cache.flyCache)
                    PlayerDataDAOUtil.startSpeedCache(p, cache.speedCache)
                }, 5L
            )

            return true
        }
    }
}
