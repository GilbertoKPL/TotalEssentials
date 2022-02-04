package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object VanishCache {
    fun PlayerData.switchVanish(player: Player?): Boolean {

        val newValue = vanishCache.not()

        vanishCache = newValue

        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.vanishTable to newValue))

        if (player != null) {
            if (newValue) {
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
                ReflectUtil.getPlayers().forEach {
                    @Suppress("DEPRECATION")
                    if (!it.hasPermission("essentialsk.commands.vanish") &&
                        !it.hasPermission("essentialsk.bypass.vanish")
                    ) {
                        it.hidePlayer(player)
                    }
                }
            } else {
                player.removePotionEffect(PotionEffectType.INVISIBILITY)
                ReflectUtil.getPlayers().forEach {
                    @Suppress("DEPRECATION")
                    it.showPlayer(player)
                }
            }
        }

        return newValue
    }
}