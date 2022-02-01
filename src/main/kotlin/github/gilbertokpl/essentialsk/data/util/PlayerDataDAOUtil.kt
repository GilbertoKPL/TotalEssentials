package github.gilbertokpl.essentialsk.data.util

import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.data.dao.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

internal object PlayerDataDAOUtil {

    fun createEmptyCache(p: Player, playerID: String, limitHome: Int): PlayerData {
        return PlayerData(
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
        val cache = PlayerData[p] ?: return
        startGamemodeCache(p, cache.gameModeCache)
        startVanishCache(p, cache.vanishCache)
        startLightCache(p, cache.lightCache)
        startFlyCache(p, cache.flyCache)
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

    //speed
    fun startSpeedCache(p: Player, speed: Int): Int {
        if (speed == 1) {
            return speed
        }
        p.walkSpeed = (speed * 0.1).toFloat()
        p.flySpeed = (speed * 0.1).toFloat()
        return speed
    }
}
