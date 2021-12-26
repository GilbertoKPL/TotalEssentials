package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorldEvent : Listener {
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
        val p = PlayerData(e.player.name)
        val gm = PlayerUtil.getInstance().getGamemodeNumber(p.getGamemode().toString())
        if (gm != e.player.gameMode) {
            e.player.gameMode = gm
        }
        if (p.checkFly()) {
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