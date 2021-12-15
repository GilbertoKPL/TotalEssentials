package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleEnterEvent

class PlayerVehicleEnterEvent : Listener {
    @EventHandler
    fun event(e: VehicleEnterEvent) {
        if (MainConfig.getInstance().antibugsBlockClimbingOnVehicles) {
            try {
                blockEnterInVehicles(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockEnterInVehicles(e: VehicleEnterEvent) {
        if (e.entered is Player &&
            !e.entered.hasPermission("essentialsk.resources.bypass.vehicles")) {
            e.isCancelled = true
        }
    }
}