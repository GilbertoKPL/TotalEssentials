package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object NickCache {
    fun PlayerData.setNick(newNick: String, player: Player?, other: Boolean = false): Boolean {
        if (!other && player != null) {
            val exist = PlayerData.nickMap().contains(newNick.lowercase())

            if (!MainConfig.nicksCanPlayerHaveSameNick &&
                !player.hasPermission("essentialsk.bypass.nickblockednicks") &&
                exist
            ) {
                return true
            }

            player.setDisplayName(newNick)
        } else {
            player?.setDisplayName(newNick)
            nickCache = newNick
        }
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.nickTable to newNick))
        return false
    }

    fun PlayerData.delNick(player: Player?) {
        nickCache = ""
        player?.setDisplayName(player.name)
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.nickTable to ""))
    }
}