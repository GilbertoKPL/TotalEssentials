package github.gilbertokpl.total.stackmobs

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.metadata.FixedMetadataValue

object StackMobsManager {

    private const val STACK_METADATA_KEY = "stack"

    fun handleSpawnWithinRange(entity: LivingEntity) {
        if (isWorldBlocked(entity.world.name)) return

        val nearbyStackedMob = findNearbyStackedMob(entity)

        if (nearbyStackedMob != null) {
            stackMobsWithinRange(nearbyStackedMob, entity)
        } else if (!entity.hasMetadata(STACK_METADATA_KEY)) {
            initializeNewStack(entity)
        }
    }

    private fun isWorldBlocked(worldName: String): Boolean {
        return MainConfig.stackmobsBlockedWorlds.any {
            it.equals(worldName, ignoreCase = true)
        }
    }

    private fun findNearbyStackedMob(entity: LivingEntity): LivingEntity? {
        val radius = MainConfig.stackmobsRadius.toDouble()
        val radiusSquared = radius * radius
        val entityLocation = entity.location

        // Use world.entities instead of getNearbyEntities to avoid MCPCTotal entity tracking issues
        return entity.world.entities
            .filterIsInstance<LivingEntity>()
            .filter { nearby ->
                if (nearby == entity || nearby.isDead) return@filter false
                if (nearby.type != entity.type) return@filter false
                if (!nearby.hasMetadata(STACK_METADATA_KEY)) return@filter false

                // Manual distance check
                val nearbyLocation = nearby.location
                if (nearbyLocation.world != entityLocation.world) return@filter false

                val distanceSquared = entityLocation.distanceSquared(nearbyLocation)
                distanceSquared <= radiusSquared
            }
            .firstOrNull()
    }

    private fun stackMobsWithinRange(target: LivingEntity, source: LivingEntity) {
        val currentStack = target.getMetadata(STACK_METADATA_KEY).firstOrNull()?.asInt() ?: 1
        val newStackSize = currentStack + 1

        if (newStackSize > MainConfig.stackmobsMax) return

        updateStackMetadata(target, newStackSize)
        updateCustomName(target, newStackSize)

        source.remove()
    }

    private fun initializeNewStack(entity: LivingEntity) {
        val entityName = getEntityName(entity)
        mobCreate(entity, 1, entityName)
    }

    /**
     * Cria/inicializa um mob stackado com nome customizado
     */
    fun mobCreate(entity: Entity, quantity: Int, displayName: String) {
        if (entity !is LivingEntity) return

        updateStackMetadata(entity, quantity)
        updateCustomName(entity, quantity, displayName)
    }

    /**
     * Reduz o stack de um mob existente (quando leva dano letal)
     * Incrementa o contador de mortes para multiplicar drops no final
     */
    fun reduceStack(entity: LivingEntity, newStackSize: Int) {
        updateStackMetadata(entity, newStackSize)
        updateCustomName(entity, newStackSize)
    }

    /**
     * Cria um novo mob stackado a partir de um que morreu (fallback do EntityDeath)
     * Preserva o contador de mortes do mob original
     */
    fun respawnStack(entity: LivingEntity, newStackSize: Int) {
        val displayName = getEntityName(entity)
        updateStackMetadata(entity, newStackSize)
        updateCustomName(entity, newStackSize, displayName)
    }

    private fun updateStackMetadata(
        entity: LivingEntity,
        stackSize: Int) {
        entity.setMetadata(
            STACK_METADATA_KEY,
            FixedMetadataValue(TotalEssentials.getInstance(), stackSize)
        )
    }

    private fun updateCustomName(
        entity: LivingEntity,
        quantity: Int,
        displayName: String = getEntityName(entity)
    ) {
        val customName = MainConfig.stackmobsNameTag
            .replace("%name%", displayName)
            .replace("%quantity%", quantity.toString())

        entity.customName = customName
        entity.isCustomNameVisible = true
    }

    @Suppress("DEPRECATION")
    fun getEntityName(entity: LivingEntity): String {
        val entityTypeId = entity.type.typeId.toInt()

        val customName = MainConfig.stackmobsNameReplacer
            .firstOrNull { it.startsWith("$entityTypeId:") }
            ?.split(":")
            ?.getOrNull(1)
            ?.replace("&", "§")

        return customName ?: entity.toString().replace("Craft", "")
    }
}