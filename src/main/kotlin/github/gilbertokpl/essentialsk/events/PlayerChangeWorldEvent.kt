package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorldEvent : Listener {
    @EventHandler
    fun event(e: PlayerChangedWorldEvent) {
        if (MainConfig.getInstance().flyActivated) {
            try {
                flyEvent(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    //fly command
    private fun flyEvent(e: PlayerChangedWorldEvent) {
        val p = PlayerData(e.player.name)
        if (!p.checkFly()) {
            return
        }
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