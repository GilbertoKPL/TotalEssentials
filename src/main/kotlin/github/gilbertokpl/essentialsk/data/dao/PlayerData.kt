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
import github.gilbertokpl.essentialsk.serializator.internal.HomeSerializer
import github.gilbertokpl.essentialsk.serializator.internal.KitSerializer
import github.gilbertokpl.essentialsk.serializator.internal.LocationSerializer
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
    val coolDownCommand: HashMap<String, Long> = HashMap(10),
    val kitsCache: HashMap<String, Long> = HashMap(30),
    val homeCache: HashMap<String, Location> = HashMap(30),
    var homeLimitCache: Int = MainConfig.homesDefaultLimitHomes,
    var nickCache: String = "",
    var gameModeCache: Int = 0,
    var vanishCache: Boolean = false,
    var lightCache: Boolean = false,
    var flyCache: Boolean = false,
    var backLocation: Location? = null,
    var speedCache: Int = 1,
    var inTeleport: Boolean = false,
    var inInvsee: Player? = null
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
        PlayerDataSQL.put(playerID, hashMapOf(kitsTable to KitSerializer.serialize(kitsCache)))
    }

    fun setHome(name: String, loc: Location) {
        homeCache[name] = loc
        PlayerDataSQL.put(playerID, hashMapOf(homeTable to HomeSerializer.serialize(homeCache)))
    }

    fun delHome(name: String) {
        homeCache.remove(name)
        PlayerDataSQL.put(playerID, hashMapOf(homeTable to HomeSerializer.serialize(homeCache)))
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
        PlayerDataSQL.put(playerID, hashMapOf(backTable to LocationSerializer.serialize(loc)))
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

                    lateinit var playerData: PlayerData

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
                                PlayerData(
                                    player = p,
                                    playerID = playerID,
                                    homeLimitCache = limitHome
                                )
                            return@transaction
                        }

                        val single = query.single()

                        playerData = PlayerData(
                            playerID = playerID,
                            player = p,
                            kitsCache = KitSerializer.deserialize(single[kitsTable]),
                            homeCache = HomeSerializer.deserialize(single[homeTable]),
                            homeLimitCache = limitHome,
                            nickCache = single[nickTable],
                            gameModeCache = single[gameModeTable],
                            vanishCache = single[vanishTable],
                            lightCache = single[lightTable],
                            flyCache = single[flyTable],
                            backLocation = LocationSerializer.deserialize(single[backTable]),
                            speedCache = single[speedTable],
                        )
                    }

                    if (empty) return@schedule

                    waitFor(10)
                    switchContext(SynchronizationContext.SYNC)

                    playerCacheV2[p.name.lowercase()] = playerData

                    setValuesPlayer(playerData)

                    PlayerUtil.finishLogin(p, playerData.vanishCache)

                    return@schedule
                }

                cache.homeLimitCache = limitHome
                cache.player = p
                cache.inTeleport = false
                cache.inInvsee = null

                PlayerUtil.sendToSpawn(p)

                PlayerUtil.finishLogin(p, cache.vanishCache)

                waitFor(10)

                setValuesPlayer(cache)
            }
        }

        fun setValuesPlayer(playerData: PlayerData) {

            val p = playerData.player

            if (playerData.nickCache != "" && playerData.nickCache != p.displayName) {
                p.setDisplayName(playerData.nickCache)
            }

            val gameModeName = PlayerUtil.getGamemodeNumber(playerData.gameModeCache.toString())

            if (playerData.player.gameMode != gameModeName) {
                p.gameMode = gameModeName
            }

            if (playerData.vanishCache) {
                p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
                for (it in ReflectUtil.getPlayers()) {
                    if (it.player!!.hasPermission("essentialsk.commands.vanish")
                        || it.player!!.hasPermission("essentialsk.bypass.vanish")
                    ) {
                        continue
                    }
                    @Suppress("DEPRECATION")
                    it.hidePlayer(playerData.player)
                }
            }

            if (playerData.lightCache) {
                p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
            }

            if (playerData.flyCache) {
                p.allowFlight = true
                p.isFlying = true
            }

            if (playerData.speedCache != 1) {
                p.walkSpeed = (playerData.speedCache * 0.1).toFloat()
                p.flySpeed = (playerData.speedCache * 0.1).toFloat()
            }
        }
    }
}
