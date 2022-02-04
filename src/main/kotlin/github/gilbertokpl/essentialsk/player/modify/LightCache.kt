package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object LightCache {
    fun PlayerData.switchLight(player: Player?): Boolean {

        val newValue = lightCache.not()

        lightCache = newValue

        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.lightTable to newValue))

        if (player != null) {
            if (newValue) {
                player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
            } else {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION)
            }
        }

        return newValue
    }
}