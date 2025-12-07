package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleEnterEvent

class EntityVehicleEnter : Listener {

    @EventHandler
    fun onVehicleEnter(event: VehicleEnterEvent) {
        if (!MainConfig.antibugsBlockClimbingOnVehicles) return

        val player = event.entered as? Player ?: return

        if (player.hasPermission("totalessentials.bypass.vehicles")) return

        player.sendMessage(LangConfig.generalNotPermAction)
        event.isCancelled = true
    }
}