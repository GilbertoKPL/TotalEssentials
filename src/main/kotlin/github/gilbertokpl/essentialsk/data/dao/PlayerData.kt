package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.put
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
import github.gilbertokpl.essentialsk.data.util.PlayerDataDAOUtil
import github.gilbertokpl.essentialsk.data.util.Serializator
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import github.okkero.skedule.SynchronizationContext
import github.okkero.skedule.schedule
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

internal data class PlayerData(
    val playerID: String,
    var player: Player,
    val coolDownCommand: HashMap<String, Long>,
    val kitsCache: HashMap<String, Long>,
    val homeCache: HashMap<String, Location>,
    var homeLimitCache: Int,
    var nickCache: String,
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
        PlayerDataSQL.put(playerID, hashMapOf(kitsTable to Serializator.playerDataKit(kitsCache)))
    }

    fun setHome(name: String, loc: Location) {
        homeCache[name] = loc
        PlayerDataSQL.put(playerID, hashMapOf(homeTable to Serializator.playerDataHome(homeCache)))
    }

    fun delHome(name: String) {
        homeCache.remove(name)
        PlayerDataSQL.put(playerID, hashMapOf(homeTable to Serializator.playerDataHome(homeCache)))
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
            transaction(DataManager.sql) {
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
            nickCache = newNick
        }
        PlayerDataSQL.put(playerID, hashMapOf(nickTable to newNick))
        return false
    }

    fun delNick() {
        nickCache = ""
        player.setDisplayName(player.name)
        PlayerDataSQL.put(playerID, hashMapOf(nickTable to ""))
    }

    //gamemode

    fun setGamemode(gm: GameMode) {
        player.gameMode = gm

        val gamemodeNumber = PlayerUtil.getNumberGamemode(gm)
        gameModeCache = gamemodeNumber

        //desbug fly on set gamemode 0
        if (gm == GameMode.SURVIVAL && flyCache) {
            player.allowFlight = true
            player.isFlying = true
        }

        PlayerDataSQL.put(playerID, hashMapOf(gameModeTable to gamemodeNumber))
    }


    //vanish

    fun switchVanish(): Boolean {

        val newValue = vanishCache.not()

        vanishCache = newValue

        PlayerDataSQL.put(playerID, hashMapOf(vanishTable to newValue))

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

        PlayerDataSQL.put(playerID, hashMapOf(lightTable to newValue))

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

        PlayerDataSQL.put(playerID, hashMapOf(flyTable to newValue))

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
        PlayerDataSQL.put(playerID, hashMapOf(backTable to Serializator.locationSerializer(loc)))
    }

    fun clearBack() {
        backLocation = null
        PlayerDataSQL.put(playerID, hashMapOf(backTable to ""))
    }

    //speed

    fun setSpeed(vel: Int) {
        player.walkSpeed = (vel * 0.1).toFloat()
        player.flySpeed = (vel * 0.1).toFloat()
        speedCache = vel
        PlayerDataSQL.put(playerID, hashMapOf(speedTable to vel))
    }

    fun clearSpeed() {
        player.walkSpeed = 0.2F
        player.flySpeed = 0.1F
        speedCache = 1
        PlayerDataSQL.put(playerID, hashMapOf(speedTable to 1))
    }

    companion object {

        private val playerCacheV2 = HashMap<String, PlayerData>()

        operator fun get(p: Player) = playerCacheV2[PlayerUtil.getPlayerUUID(p)]

        operator fun get(playerID: String) = playerCacheV2[playerID]

        fun getValues() = playerCacheV2.values

        fun loadCache(e: PlayerJoinEvent) {
            val p = e.player

            val limitHome: Int = PermissionUtil.getNumberPermission(
                p,
                "essentialsk.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )

            Bukkit.getScheduler().schedule(EssentialsK.instance) {

                val cache = get(p)

                if (cache == null) {

                    PlayerUtil.sendToSpawn(p)

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

                    switchContext(SynchronizationContext.ASYNC)

                    var empty = false

                    transaction(DataManager.sql) {
                        val query = PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }

                        empty = query.empty()

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

                    if (empty) return@schedule

                    waitFor(10)
                    switchContext(SynchronizationContext.SYNC)

                    playerCacheV2[p.name.lowercase()] = PlayerData(
                        playerID = playerID,
                        player = p,
                        coolDownCommand = HashMap(5),
                        kitsCache = Serializator.playerDataKit(kitsList),
                        homeCache = Serializator.playerDataHome(homesList),
                        homeLimitCache = limitHome,
                        nickCache = PlayerDataDAOUtil.startNickCache(p, nick),
                        gameModeCache = PlayerDataDAOUtil.startGamemodeCache(p, gameMode),
                        vanishCache = PlayerDataDAOUtil.startVanishCache(p, vanish),
                        lightCache = PlayerDataDAOUtil.startLightCache(p, light),
                        flyCache = PlayerDataDAOUtil.startFlyCache(p, fly),
                        backLocation = Serializator.locationSerializer(location),
                        speedCache = PlayerDataDAOUtil.startSpeedCache(p, speed),
                        inTeleport = false
                    )


                    PlayerUtil.finishLogin(p, vanish)

                    return@schedule
                }

                cache.homeLimitCache = limitHome
                cache.player = p

                PlayerUtil.sendToSpawn(p)

                PlayerUtil.finishLogin(p, cache.vanishCache)

                waitFor(10)

                PlayerDataDAOUtil.startNickCache(p, cache.nickCache)
                PlayerDataDAOUtil.startGamemodeCache(p, cache.gameModeCache)
                PlayerDataDAOUtil.startVanishCache(p, cache.vanishCache)
                PlayerDataDAOUtil.startLightCache(p, cache.lightCache)
                PlayerDataDAOUtil.startFlyCache(p, cache.flyCache)
                PlayerDataDAOUtil.startSpeedCache(p, cache.speedCache)
            }
        }
    }
}
