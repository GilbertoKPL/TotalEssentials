package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class CreatureSpawn : Listener {
    @EventHandler
    fun event(e: CreatureSpawnEvent) {
        if (MainConfig.antibugsBlockMobCatch) {
            try {
                blockMobCatch(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockMobCatch(e: CreatureSpawnEvent) {
        e.entity.canPickupItems = false
    }
}
