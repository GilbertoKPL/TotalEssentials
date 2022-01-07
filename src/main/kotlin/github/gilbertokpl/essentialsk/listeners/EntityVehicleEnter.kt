package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleEnterEvent

class EntityVehicleEnter : Listener {
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
            !e.entered.hasPermission("essentialsk.bypass.vehicles")
        ) {
            e.entered.sendMessage(GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
        }
    }
}
