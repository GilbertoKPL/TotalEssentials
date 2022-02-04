package github.gilbertokpl.essentialsk.player.loader

import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object PlayerSet {
    fun Player.values(playerData: PlayerData) {

        val p = player!!

        if (playerData.nickCache != "" && playerData.nickCache != p.displayName) {
            p.setDisplayName(playerData.nickCache)
        }

        val gameModeName = PlayerUtil.getGamemodeNumber(playerData.gameModeCache.toString())

        if (p.gameMode != gameModeName) {
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
                it.hidePlayer(p)
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