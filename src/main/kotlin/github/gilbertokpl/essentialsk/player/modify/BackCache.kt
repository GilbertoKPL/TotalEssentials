package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.serializator.internal.LocationSerializer
import org.bukkit.Location

object BackCache {
    fun PlayerData.setBack(loc: Location) {
        backLocation = loc
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.backTable to LocationSerializer.serialize(loc)))
    }

    fun PlayerData.clearBack() {
        backLocation = null
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.backTable to ""))
    }
}