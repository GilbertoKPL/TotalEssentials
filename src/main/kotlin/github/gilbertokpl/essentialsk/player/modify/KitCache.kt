package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.serializator.internal.KitSerializer

object KitCache {
    fun PlayerData.setKitTime(kit: String, time: Long) {
        kitsCache[kit] = time
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.kitsTable to KitSerializer.serialize(kitsCache)))
    }
}