package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent

class PlayerBedEnterEvent : Listener {
    @EventHandler
    fun event(e: PlayerBedEnterEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().antibugsBlockBed) {
            try {
                blockEnterInBed(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockEnterInBed(e: PlayerBedEnterEvent) {
        if (!e.player.hasPermission("cardinal.bypass.bed")) {
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
        }
    }
}