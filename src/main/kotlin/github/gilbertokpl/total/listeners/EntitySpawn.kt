package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.StackMobsUtil.handleSpawnWithinRange
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class EntitySpawn : Listener {
    @EventHandler
    fun event(e: CreatureSpawnEvent) {
        if (MainConfig.stackmobsActivated) {
            try {
                stackMobsEvent(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
    private fun stackMobsEvent(e: CreatureSpawnEvent) {
        val ent = e.entity
        val stack = ent.hasMetadata("stack")
        val reason = e.spawnReason
        if (reason == CreatureSpawnEvent.SpawnReason.EGG || (reason == CreatureSpawnEvent.SpawnReason.CUSTOM && !stack)) {
            return
        }
        if (!MainConfig.stackmobsStackList.contains(ent.type.typeId.toInt())) {
            return
        }
        handleSpawnWithinRange(ent)
    }
}