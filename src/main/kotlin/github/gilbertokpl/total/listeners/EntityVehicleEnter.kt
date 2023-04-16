package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
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
                e.printStackTrace()
            }
        }
    }

    private fun blockEnterInVehicles(e: VehicleEnterEvent) {
        if (e.entered is Player &&
            !(e.entered as Player).hasPermission("totalessentials.bypass.vehicles")
        ) {
            (e.entered as Player).sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
