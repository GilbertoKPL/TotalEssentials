package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.serializator.internal.HomeSerializer
import org.bukkit.Location

object HomeCache {
    fun PlayerData.setHome(name: String, loc: Location) {
        homeCache[name] = loc
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.homeTable to HomeSerializer.serialize(homeCache)))
    }

    fun PlayerData.delHome(name: String) {
        homeCache.remove(name)
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.homeTable to HomeSerializer.serialize(homeCache)))
    }

    fun PlayerData.getHomeList(): List<String> {
        return homeCache.map { it.key }
    }

    fun PlayerData.getHomeLocation(home: String): Location? {
        return homeCache[home.lowercase()]
    }
}