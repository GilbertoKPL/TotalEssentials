package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorld : Listener {
    @EventHandler
    fun event(e: PlayerChangedWorldEvent) {
        if (MainConfig.flyActivated) {
            try {
                data(e)
            } catch (e: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun data(e: PlayerChangedWorldEvent) {
        val playerCache = PlayerDataV2[e.player] ?: return
        val gm = PlayerUtil.getGamemodeNumber(playerCache.gameModeCache.toString())
        Bukkit.getScheduler().runTaskLater(EssentialsK.instance, Runnable {
            if (gm != e.player.gameMode) {
                e.player.gameMode = gm
            }
            if (playerCache.flyCache) {
                if (!MainConfig.flyDisabledWorlds.contains(e.player.world.name)) {
                    e.player.allowFlight = true
                    e.player.isFlying = true
                    return@Runnable
                }
                e.player.sendMessage(GeneralLang.flySendDisabledWorld)
                e.player.allowFlight = false
                e.player.isFlying = false
            }
        }, 5L)
    }
}
