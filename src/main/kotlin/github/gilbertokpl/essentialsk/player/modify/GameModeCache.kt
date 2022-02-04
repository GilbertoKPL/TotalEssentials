package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.bukkit.GameMode
import org.bukkit.entity.Player

object GameModeCache {
    fun PlayerData.setGamemode(gm: GameMode, player: Player?) {

        val gamemodeNumber = PlayerUtil.getNumberGamemode(gm)

        gameModeCache = gamemodeNumber
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.gameModeTable to gamemodeNumber))

        if (player != null) {
            player.gameMode = gm

            //desbug fly on set gamemode 0
            if (gm == GameMode.SURVIVAL && flyCache) {
                player.allowFlight = true
                player.isFlying = true
            }
        }

    }
}