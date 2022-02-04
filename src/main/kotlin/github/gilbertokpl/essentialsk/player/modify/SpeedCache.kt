package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import org.bukkit.entity.Player

object SpeedCache {
    fun PlayerData.setSpeed(vel: Int, player: Player?) {
        speedCache = vel
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.speedTable to vel))

        if (player != null) {
            player.walkSpeed = (vel * 0.1).toFloat()
            player.flySpeed = (vel * 0.1).toFloat()
        }
    }

    fun PlayerData.clearSpeed(player: Player?) {
        speedCache = 1
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.speedTable to 1))

        if (player != null) {
            player.walkSpeed = 0.2F
            player.flySpeed = 0.1F
        }
    }
}