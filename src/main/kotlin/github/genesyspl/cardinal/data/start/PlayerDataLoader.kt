package github.genesyspl.cardinal.data.start

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.PlayerDataV2
import github.genesyspl.cardinal.data.`object`.SpawnData
import github.genesyspl.cardinal.manager.IInstance
import github.genesyspl.cardinal.tables.PlayerDataSQL
import github.genesyspl.cardinal.tables.PlayerDataSQL.PlayerInfo
import github.genesyspl.cardinal.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PlayerDataLoader {

    private fun createEmptyCache(p: Player, playerID: String, limitHome: Int): PlayerDataV2 {
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
        val cache = DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!
        startGamemodeCache(p, cache.gameModeCache)
        startVanishCache(p, cache.vanishCache)
        startLightCache(p, cache.lightCache)
        startFlyCache(p, cache.flyCache)
    }


    //kits
    private fun startKitCache(timeKits: String): HashMap<String, Long> {
        val cacheKits = HashMap<String, Long>(40)
        for (kits in timeKits.split("|")) {
            try {
                if (kits != "") {
                    val split = kits.split(",")
                    val timeKit = split[1].toLong()
                    val nameKit = split[0]

                    val kitsCache = DataManager.getInstance().kitCacheV2[nameKit]

                    if (kitsCache != null) {
                        val timeAll = kitsCache.time

                        if ((timeKit != 0L) && ((timeAll + timeKit) > System.currentTimeMillis())) {
                            cacheKits[nameKit] = timeKit
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


    //home
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

    //nick
    private fun startNickCache(p: Player, fakeNick: String): String {
        if (fakeNick != "") {
            p.setDisplayName(fakeNick)
        }
        return fakeNick
    }

    //gamemode
    private fun startGamemodeCache(p: Player, gameMode: Int): Int {
        val gameModeName = PlayerUtil.getInstance().getGamemodeNumber(gameMode.toString())

        if (p.gameMode != gameModeName) {
            p.gameMode = gameModeName
        }

        return gameMode
    }


    //vanish
    private fun startVanishCache(p: Player, active: Boolean): Boolean {
        if (active) {
            p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            for (it in ReflectUtil.getInstance().getPlayers()) {
                if (it.player!!.hasPermission("cardinal.commands.vanish")
                    || it.player!!.hasPermission("cardinal.bypass.vanish")
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
    private fun startLightCache(p: Player, active: Boolean): Boolean {
        if (active) {
            p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
        }
        return active
    }


    //fly
    private fun startFlyCache(p: Player, active: Boolean): Boolean {
        if (active) {
            p.allowFlight = true
            p.isFlying = true
        }
        return active
    }

    //back
    private fun startBackCache(loc: String): Location? {
        return LocationUtil.getInstance().locationSerializer(loc)
    }

    //speed
    private fun startSpeedCache(p: Player, speed: Int): Int {
        if (speed == 1) {
            return speed
        }
        p.walkSpeed = (speed * 0.1).toFloat()
        p.flySpeed = (speed * 0.1).toFloat()
        return speed
    }


    //load

    fun saveCache(p: Player) {
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                val playerID = PlayerUtil.getInstance().getPlayerUUID(p)
                val cache = DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!
                PlayerDataSQL.update({ PlayerInfo eq playerID }) {
                    it[GameMode] = cache.gameModeCache
                    it[Vanish] = cache.vanishCache
                    it[Light] = cache.lightCache
                    it[Fly] = cache.flyCache
                    it[Back] =
                        cache.backLocation?.let { it1 -> LocationUtil.getInstance().locationSerializer(it1) } ?: ""
                }
            }
        }
    }

    fun loadCache(p: Player) {
        val playerID = PlayerUtil.getInstance().getPlayerUUID(p)

        val limitHome: Int = PermissionUtil.getInstance().getNumberPermission(
            p,
            "cardinal.commands.sethome.",
            github.genesyspl.cardinal.configs.MainConfig.getInstance().homesDefaultLimitHomes
        )

        if (DataManager.getInstance().playerCacheV2.containsKey(p.name.lowercase())) {
            val cache = DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!
            cache.homeLimitCache = limitHome
            cache.player = p

            github.genesyspl.cardinal.Cardinal.instance.server.scheduler.runTaskLater(
                github.genesyspl.cardinal.Cardinal.instance,
                Runnable {
                    startNickCache(p, cache.fakeNickCache)
                    startGamemodeCache(p, cache.gameModeCache)
                    startVanishCache(p, cache.vanishCache)
                    startLightCache(p, cache.lightCache)
                    startFlyCache(p, cache.flyCache)
                    startSpeedCache(p, cache.speedCache)
                },
                5L
            )

            if (!cache.vanishCache) {
                if (github.genesyspl.cardinal.configs.MainConfig.getInstance().messagesLoginMessage) {
                    PluginUtil.getInstance().serverMessage(
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().messagesEnterMessage
                            .replace("%player%", p.name)
                    )
                }
                if (github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotSendLoginMessage) {
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
        var loc = ""
        var speed = 1

        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerInfo eq playerID }.also { query ->
                    if (query.empty()) {
                        PlayerDataSQL.insert {
                            it[PlayerInfo] = playerID
                        }
                        DataManager.getInstance().playerCacheV2[playerID] = createEmptyCache(p, playerID, limitHome)
                        return@transaction
                    }
                    timeKits = query.single()[PlayerDataSQL.KitsTime]
                    homesList = query.single()[PlayerDataSQL.SavedHomes]
                    fakeNick = query.single()[PlayerDataSQL.FakeNick]
                    gameMode = query.single()[PlayerDataSQL.GameMode]
                    vanish = query.single()[PlayerDataSQL.Vanish]
                    light = query.single()[PlayerDataSQL.Light]
                    fly = query.single()[PlayerDataSQL.Fly]
                    loc = query.single()[PlayerDataSQL.Back]
                    speed = query.single()[PlayerDataSQL.Speed]
                }
            }

            github.genesyspl.cardinal.Cardinal.instance.server.scheduler.runTask(
                github.genesyspl.cardinal.Cardinal.instance,
                Runnable {
                    DataManager.getInstance().playerCacheV2[p.name.lowercase()] = PlayerDataV2(
                        playerID = playerID,
                        player = p,
                        coolDownCommand = HashMap(5),
                        kitsCache = startKitCache(timeKits),
                        homeCache = startHomeCache(homesList),
                        homeLimitCache = limitHome,
                        fakeNickCache = startNickCache(p, fakeNick),
                        gameModeCache = startGamemodeCache(p, gameMode),
                        vanishCache = startVanishCache(p, vanish),
                        lightCache = startLightCache(p, light),
                        flyCache = startFlyCache(p, fly),
                        backLocation = startBackCache(loc),
                        speedCache = startSpeedCache(p, speed)
                    )

                    if (!vanish) {
                        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().messagesLoginMessage) {
                            PluginUtil.getInstance().serverMessage(
                                github.genesyspl.cardinal.configs.GeneralLang.getInstance().messagesEnterMessage
                                    .replace("%player%", p.name)
                            )
                        }
                        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotSendLoginMessage) {
                            PlayerUtil.getInstance().sendLoginEmbed(p)
                        }
                    }

                    val spawn = SpawnData("spawn")
                    if (!spawn.checkCache()) {
                        p.teleport(SpawnData("spawn").getLocation())
                    }
                })
        }
    }

    companion object : IInstance<PlayerDataLoader> {
        private val instance = createInstance()
        override fun createInstance(): PlayerDataLoader = PlayerDataLoader()
        override fun getInstance(): PlayerDataLoader {
            return instance
        }
    }
}