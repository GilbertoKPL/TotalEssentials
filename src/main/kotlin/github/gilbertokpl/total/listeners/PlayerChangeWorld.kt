package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorld : Listener {
    @EventHandler
    fun event(e: PlayerChangedWorldEvent) {
        if (MainConfig.flyActivated) {
            try {
                data(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun data(e: PlayerChangedWorldEvent) {
        val gm = PlayerUtil.getGameModeNumber(PlayerData.gameModeCache[e.player].toString())
        github.gilbertokpl.total.TotalEssentials.basePlugin.getTask().async {
            waitFor(20)
            if (gm != e.player.gameMode) {
                e.player.gameMode = gm
            }
            if (PlayerData.flyCache[e.player]!! && PlayerData.gameModeCache[e.player]!! == 0) {
                if (!MainConfig.flyDisabledWorlds.contains(e.player.world.name)) {
                    e.player.allowFlight = true
                    e.player.isFlying = true
                    return@async
                }
                e.player.sendMessage(LangConfig.flyDisabledWorld)
                e.player.allowFlight = false
                e.player.isFlying = false
            }
        }
    }
}
