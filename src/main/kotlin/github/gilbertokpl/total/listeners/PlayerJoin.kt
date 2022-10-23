package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData
import github.gilbertokpl.total.cache.SpawnData
import github.gilbertokpl.total.util.PermissionUtil
import github.gilbertokpl.total.util.PlayerUtil

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerJoinEvent) {
        e.joinMessage = null

        github.gilbertokpl.total.TotalEssentials.basePlugin.getTask().async {
            val p = e.player

            waitFor(5)

            SpawnData.teleport(p)

            val limitHome: Int = PermissionUtil.getNumberPermission(
                p,
                "essentialsk.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )


            if (!PlayerData.checkIfPlayerExist(p)) {
                PlayerData.createNewPlayerData(e.player.name)
            }

            PlayerData.homeLimitCache[p] = limitHome

            PlayerData.values(e.player)

            PlayerUtil.finishLogin(p, PlayerData.vanishCache[p]!!)

        }
    }
}
