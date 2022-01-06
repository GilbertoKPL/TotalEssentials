package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class CreatureSpawnEvent : Listener {
    @EventHandler
    fun event(e: CreatureSpawnEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().antibugsBlockMobCatch) {
            try {
                blockMobCatch(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockMobCatch(e: CreatureSpawnEvent) {
        e.entity.canPickupItems = false
    }
}