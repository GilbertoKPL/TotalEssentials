package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.MainUtil
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
                "totalessentials.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )


            if (!PlayerData.checkIfPlayerExist(p)) {
                PlayerData.createNewPlayerData(e.player.name)
            }

            PlayerData.homeLimitCache[p] = limitHome

            PlayerData.values(e.player)

            if (!p.hasPermission("*")) {
                if (MainConfig.messagesLoginMessage) {
                    MainUtil.serverMessage(
                        LangConfig.messagesEnterMessage
                            .replace("%player%", p.name)
                    )
                }
                if (MainConfig.discordbotSendLoginMessage) {
                    //discord
                }
            }

        }
    }
}
