package github.gilbertokpl.essentialsk.data.objects

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.sql.PlayerDataSQLUtil
import github.gilbertokpl.essentialsk.data.start.PlayerDataLoader
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
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

data class PlayerDataV2(
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
                exist = PlayerDataSQL.select { FakeNick eq newNick }.empty()
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
        SqlUtil.helperUpdater(playerID, FakeNick, newNick)
        return false
    }

    fun delNick() {
        //cache
        fakeNickCache = ""

        player.setDisplayName(player.name)

        //sql
        SqlUtil.helperUpdater(playerID, FakeNick, "")
    }

    //gamemode

    fun setGamemode(gm: GameMode, value: Int) {
        player.gameMode = gm

        gameModeCache = value

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
                    !it.hasPermission("essentialsk.bypass.vanish")) {
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

        private val playerCacheV2 = mutableMapOf<String, PlayerDataV2>()

        operator fun get(p: Player) = playerCacheV2[p.name.lowercase()]

        fun loadCache(e: PlayerJoinEvent) {
            val p = e.player

            val limitHome: Int = PermissionUtil.getNumberPermission(
                p,
                "essentialsk.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )

            val cache = get(p) ?: run {
                val playerID = PlayerUtil.getPlayerUUID(p)
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

                TaskUtil.asyncExecutor {
                    transaction(SqlUtil.sql) {
                        PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.also { query ->
                            if (query.empty()) {
                                PlayerDataSQL.insert {
                                    it[PlayerInfo] = playerID
                                }
                                playerCacheV2[p.name.lowercase()] =
                                    PlayerDataLoader.createEmptyCache(p, playerID, limitHome)
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

                    EssentialsK.instance.server.scheduler.runTaskLater(EssentialsK.instance, Runnable {
                        playerCacheV2[p.name.lowercase()] = PlayerDataV2(
                            playerID = playerID,
                            player = p,
                            coolDownCommand = HashMap(5),
                            kitsCache = PlayerDataLoader.startKitCache(timeKits),
                            homeCache = PlayerDataLoader.startHomeCache(homesList),
                            homeLimitCache = limitHome,
                            fakeNickCache = PlayerDataLoader.startNickCache(p, fakeNick),
                            gameModeCache = PlayerDataLoader.startGamemodeCache(p, gameMode),
                            vanishCache = PlayerDataLoader.startVanishCache(p, vanish),
                            lightCache = PlayerDataLoader.startLightCache(p, light),
                            flyCache = PlayerDataLoader.startFlyCache(p, fly),
                            backLocation = PlayerDataLoader.startBackCache(loc),
                            speedCache = PlayerDataLoader.startSpeedCache(p, speed)
                        )

                        if (!vanish && !p.hasPermission("*")) {
                            if (MainConfig.messagesLoginMessage) {
                                PluginUtil.serverMessage(
                                    GeneralLang.messagesEnterMessage
                                        .replace("%player%", p.name)
                                )
                            }
                            if (MainConfig.discordbotSendLoginMessage) {
                                PlayerUtil.sendLoginEmbed(p)
                            }
                        }

                        val spawn = SpawnData("spawn")
                        if (!spawn.checkCache()) {
                            p.teleport(SpawnData("spawn").getLocation())
                        }
                    }, 5L)
                }
                return
            }

            cache.homeLimitCache = limitHome
            cache.player = p

            EssentialsK.instance.server.scheduler.runTaskLater(EssentialsK.instance, Runnable {
                PlayerDataLoader.startNickCache(p, cache.fakeNickCache)
                PlayerDataLoader.startGamemodeCache(p, cache.gameModeCache)
                PlayerDataLoader.startVanishCache(p, cache.vanishCache)
                PlayerDataLoader.startLightCache(p, cache.lightCache)
                PlayerDataLoader.startFlyCache(p, cache.flyCache)
                PlayerDataLoader.startSpeedCache(p, cache.speedCache)
            }, 5L)

            if (!cache.vanishCache && !p.hasPermission("*")) {
                if (MainConfig.messagesLoginMessage) {
                    PluginUtil.serverMessage(
                        GeneralLang.messagesEnterMessage
                            .replace("%player%", p.name)
                    )
                }
                if (MainConfig.discordbotSendLoginMessage) {
                    PlayerUtil.sendLoginEmbed(p)
                }
            }
            return
        }
    }
}
