package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentialsJava.instance
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.StackMobsUtil
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.metadata.FixedMetadataValue

class EntityDeath : Listener {
    @EventHandler
    fun event(e: EntityDeathEvent) {
        try {
            if (MainConfig.stackmobsActivated) {
                stackMobsEvent(e)
            }
        } catch (ex: Exception) {
            // Log the exception or handle it appropriately
            ex.printStackTrace()
        }
    }

    private fun stackMobsEvent(e: EntityDeathEvent) {
        val livingEntity = e.entity as? LivingEntity ?: return

        if (livingEntity.hasMetadata("stack")) {
            val deathQuantity = livingEntity.getMetadata("DeathQuantity")[0].asInt()
            val stackSize = livingEntity.getMetadata("stack")[0].asInt()

            if (deathQuantity <= 1) {
                handleStackMobsDeath(livingEntity, stackSize)
            } else {
                adjustDropsForStackedMobs(e, deathQuantity)
            }
        }
    }

    private fun handleStackMobsDeath(livingEntity: LivingEntity, stackSize: Int) {
        if (stackSize - 1 < 1) return

        try {
            val entityId = livingEntity.type.typeId.toInt()

            val newEntity = EntityType.fromId(entityId)
                ?.let { livingEntity.world.spawnEntity(livingEntity.location, it) }

            newEntity?.setMetadata("mob_id", FixedMetadataValue(instance, entityId))

            var name = livingEntity.toString().replace("Craft", "")
            MainConfig.stackmobsNameReplacer
                .firstOrNull { it.startsWith("${livingEntity.type.typeId.toInt()}:") }?.split(":")?.get(1)?.let {
                    name = it
                }

            if (newEntity != null) {
                StackMobsUtil.mobCreate(newEntity, stackSize - 1, name)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun adjustDropsForStackedMobs(e: EntityDeathEvent, deathQuantity: Int) {
        e.drops.removeIf { it.type.maxDurability.toInt() == 0 }
        e.drops.forEach { it.amount *= deathQuantity }
    }
}
