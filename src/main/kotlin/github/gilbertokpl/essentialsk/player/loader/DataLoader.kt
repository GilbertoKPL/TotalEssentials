package github.gilbertokpl.essentialsk.player.loader

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.loader.PlayerSet.values
import github.gilbertokpl.essentialsk.player.serializator.internal.HomeSerializer
import github.gilbertokpl.essentialsk.player.serializator.internal.KitSerializer
import github.gilbertokpl.essentialsk.player.serializator.internal.LocationSerializer
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.okkero.skedule.schedule
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DataLoader {

    fun loadCache(): Int {
        var users = 0
        transaction(DataManager.sql) {
            for (i in PlayerDataSQL.selectAll()) {
                users += 1
                val playerID = i[PlayerDataSQL.playerTable]
                PlayerData[playerID] = PlayerData(
                    playerID = playerID,
                    kitsCache = KitSerializer.deserialize(i[PlayerDataSQL.kitsTable]),
                    homeCache = HomeSerializer.deserialize(i[PlayerDataSQL.homeTable]),
                    nickCache = i[PlayerDataSQL.nickTable],
                    gameModeCache = i[PlayerDataSQL.gameModeTable],
                    vanishCache = i[PlayerDataSQL.vanishTable],
                    lightCache = i[PlayerDataSQL.lightTable],
                    flyCache = i[PlayerDataSQL.flyTable],
                    backLocation = LocationSerializer.deserialize(i[PlayerDataSQL.backTable]),
                    speedCache = i[PlayerDataSQL.speedTable],
                )
            }
        }
        return users
    }

    fun loginCache(e: PlayerJoinEvent) {
        Bukkit.getScheduler().schedule(EssentialsK.instance) {
            val p = e.player

            waitFor(5)
            PlayerUtil.sendToSpawn(p)

            var cache = PlayerData[p]

            val limitHome: Int = PermissionUtil.getNumberPermission(
                p,
                "essentialsk.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )

            val playerID = PlayerUtil.getPlayerUUID(p)

            if (cache == null) {
                val playerData = PlayerData(
                    playerID = playerID,
                    homeLimitCache = limitHome
                )
                PlayerData[playerID] = playerData
                cache = playerData
                PlayerDataSQL.put(playerID, HashMap(10))
            } else {
                cache.homeLimitCache = limitHome
                cache.inTeleport = false
                cache.inInvsee = null
            }

            e.player.values(cache)

            PlayerUtil.finishLogin(p, cache.vanishCache)

        }
    }

}