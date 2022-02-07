package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleEnterEvent

class EntityVehicleEnter : Listener {
    @EventHandler
    fun event(e: VehicleEnterEvent) {
        if (MainConfig.antibugsBlockClimbingOnVehicles) {
            try {
                blockEnterInVehicles(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockEnterInVehicles(e: VehicleEnterEvent) {
        if (e.entered is Player &&
            !e.entered.hasPermission("essentialsk.bypass.vehicles")
        ) {
            e.entered.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
