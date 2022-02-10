package github.gilbertokpl.essentialsk.player

import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

data class PlayerData(
    val playerID: String,
    val coolDownCommand: HashMap<String, Long> = HashMap(10),
    val kitsCache: HashMap<String, Long> = HashMap(30),
    val homeCache: HashMap<String, Location> = HashMap(30),
    var homeLimitCache: Int = MainConfig.homesDefaultLimitHomes,
    var nickCache: String = "",
    var gameModeCache: Int = 0,
    var vanishCache: Boolean = false,
    var lightCache: Boolean = false,
    var flyCache: Boolean = false,
    var backLocation: Location? = null,
    var speedCache: Int = 1,
    var moneyCache: Double = MainConfig.moneyDefault.toDouble(),
    var inTeleport: Boolean = false,
    var inInvsee: Player? = null
) {
    companion object {

        private val playerCacheV2 = HashMap<String, PlayerData>()

        operator fun get(player: Player) = playerCacheV2[PlayerUtil.getPlayerUUID(player)]

        operator fun get(offlinePlayer: OfflinePlayer) = playerCacheV2[PlayerUtil.getPlayerUUID(offlinePlayer)]

        operator fun get(PlayerName: String) = playerCacheV2[PlayerUtil.getPlayerUUID(PlayerName)]

        operator fun set(playerID: String, value: PlayerData) {
            playerCacheV2[playerID] = value
        }

        operator fun set(p: Player, value: PlayerData) {
            playerCacheV2[PlayerUtil.getPlayerUUID(p)] = value
        }

        fun nickMap() = playerCacheV2.map { it.value.nickCache.lowercase() }

        fun moneyMap(): HashMap<String, Int> {
            val hash = HashMap<String, Int>()
            for (i in playerCacheV2) {
                hash[i.key] = i.value.moneyCache.toInt()
            }
            return hash
        }

    }
}
