package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.stackmobs.StackMobsManager
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.metadata.FixedMetadataValue

class EntityDeath : Listener {

    companion object {
        private const val STACK_METADATA_KEY = "stack"
        private const val DEATH_QUANTITY_KEY = "DeathQuantity"
        private const val STACK_PROCESSED_KEY = "StackProcessed"
        private const val MOB_ID_KEY = "mob_id"
    }

    private val plugin = TotalEssentials.getInstance()

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDeath(event: EntityDeathEvent) {
        if (!MainConfig.stackmobsActivated) return

        val entity = event.entity
        if (!entity.hasMetadata(STACK_METADATA_KEY)) return

        val stackSize = entity.getMetadata(STACK_METADATA_KEY).firstOrNull()?.asInt() ?: 1

        // Stack > 1: fallback se damage handler não processou
        if (stackSize > 1 && !entity.hasMetadata(STACK_PROCESSED_KEY)) {
            respawnStackedMob(entity, stackSize - 1)
        }
    }

    private fun respawnStackedMob(entity: LivingEntity, remainingStack: Int) {
        if (remainingStack < 1) return

        @Suppress("DEPRECATION")
        val entityTypeId = entity.type.typeId.toInt()

        val location = entity.location.clone()


        val newEntity = EntityType.fromId(entityTypeId)
            ?.let { entity.world.spawnEntity(location, it) as? LivingEntity }
            ?: return

        newEntity.setMetadata(MOB_ID_KEY, FixedMetadataValue(plugin, entityTypeId))
        newEntity.setMetadata("respawnYaw", FixedMetadataValue(plugin, location.yaw))
        newEntity.setMetadata("respawnPitch", FixedMetadataValue(plugin, location.pitch))

        StackMobsManager.respawnStack(newEntity, remainingStack)

        val finalLocation = newEntity.location.clone()
        finalLocation.yaw = location.yaw
        finalLocation.pitch = location.pitch
        newEntity.teleport(finalLocation)
    }

    private fun getCustomMobName(entityTypeId: Int, entity: LivingEntity): String {
        val customName = MainConfig.stackmobsNameReplacer
            .firstOrNull { it.startsWith("$entityTypeId:") }
            ?.split(":")
            ?.getOrNull(1)

        return customName ?: entity.toString().replace("Craft", "")
    }

}