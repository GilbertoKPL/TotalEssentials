package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorldEvent : Listener {
    @EventHandler
    fun event(e: PlayerChangedWorldEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().flyActivated) {
            try {
                data(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    //fly command
    private fun data(e: PlayerChangedWorldEvent) {
        val playerCache = DataManager.getInstance().playerCacheV2[e.player.name.lowercase()]!!
        val gm = PlayerUtil.getInstance().getGamemodeNumber(playerCache.gameModeCache.toString())
        if (gm != e.player.gameMode) {
            e.player.gameMode = gm
        }
        if (playerCache.flyCache) {
            if (!github.genesyspl.cardinal.configs.MainConfig.getInstance().flyDisabledWorlds.contains(e.player.world.name)) {
                e.player.allowFlight = true
                e.player.isFlying = true
                return
            }
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendDisabledWorld)
            e.player.allowFlight = false
            e.player.isFlying = false
        }
    }
}