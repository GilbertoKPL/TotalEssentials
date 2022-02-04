package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import org.bukkit.entity.Player

object FlyCache {
    fun PlayerData.switchFly(player: Player?): Boolean {
        val newValue = flyCache.not()

        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.flyTable to newValue))

        flyCache = newValue

        if (player !== null) {
            if (newValue) {
                player.allowFlight = true
                player.isFlying = true
            } else {
                //desbug gamemode
                if (gameModeCache != 1 && gameModeCache != 3) {
                    player.allowFlight = false
                    player.isFlying = false
                }
            }
        }

        return newValue
    }
}