package github.gilbertokpl.essentialsk.data.`object`

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.sql.PlayerDataSQLUtil
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
import github.gilbertokpl.essentialsk.util.ReflectUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
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

        TaskUtil.getInstance().asyncExecutor {
            PlayerDataSQLUtil.getInstance().setKitTimeSQL(kit, time, playerID)
        }
    }

    fun delKitTime(kit: String) {
        kitsCache.remove(kit)
    }

    fun setHome(name: String, loc: Location) {
        homeCache[name] = loc

        TaskUtil.getInstance().asyncExecutor {
            PlayerDataSQLUtil.getInstance().setHomeSQL(name, loc, playerID)
        }
    }

    fun delHome(name: String) {
        homeCache.remove(name)

        TaskUtil.getInstance().asyncExecutor {
            PlayerDataSQLUtil.getInstance().delHomeSQL(name, playerID)
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
            transaction(SqlUtil.getInstance().sql) {
                exist = PlayerDataSQL.select { FakeNick eq newNick }.empty()
            }
            if (!MainConfig.getInstance().nicksCanPlayerHaveSameNick && !player.hasPermission("essentialsk.bypass.nickblockednicks")) {
                if (!exist) {
                    return true
                }
            }
            player.setDisplayName(newNick)
        }
        if (other) {
            player.setDisplayName(newNick)
            fakeNickCache = newNick
        }
        //sql
        SqlUtil.getInstance().helperUpdater(playerID, FakeNick, newNick)
        return false
    }

    fun delNick() {
        //cache
        fakeNickCache = ""

        player.setDisplayName(player.name)

        //sql
        SqlUtil.getInstance().helperUpdater(playerID, FakeNick, "")
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
            ReflectUtil.getInstance().getPlayers().forEach {
                @Suppress("DEPRECATION")
                if (!it.hasPermission("essentialsk.commands.vanish") && !it.hasPermission("essentialsk.bypass.vanish")) {
                    it.hidePlayer(player)
                }
            }
            true
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            ReflectUtil.getInstance().getPlayers().forEach {
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
}