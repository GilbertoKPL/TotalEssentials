package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorld : Listener {
    @EventHandler
    fun event(e: PlayerChangedWorldEvent) {
        if (MainConfig.getInstance().flyActivated) {
            try {
                data(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    //fly command
    private fun data(e: PlayerChangedWorldEvent) {
        val playerCache = DataManager.getInstance().playerCacheV2[e.player.name.lowercase()] ?: return
        val gm = PlayerUtil.getInstance().getGamemodeNumber(playerCache.gameModeCache.toString())
        if (gm != e.player.gameMode) {
            e.player.gameMode = gm
        }
        if (playerCache.flyCache) {
            if (!MainConfig.getInstance().flyDisabledWorlds.contains(e.player.world.name)) {
                e.player.allowFlight = true
                e.player.isFlying = true
                return
            }
            e.player.sendMessage(GeneralLang.getInstance().flySendDisabledWorld)
            e.player.allowFlight = false
            e.player.isFlying = false
        }
    }
}
