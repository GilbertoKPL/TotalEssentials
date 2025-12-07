package github.gilbertokpl.total.stackmobs

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.metadata.FixedMetadataValue

object StackMobsManager {

    private const val STACK_METADATA_KEY = "stack"
    private const val DEATH_QUANTITY_KEY = "DeathQuantity"
    private const val DEFAULT_DEATH_QUANTITY = 1

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
        val nearbyEntities = entity.getNearbyEntities(radius, radius, radius)

        return nearbyEntities
            .filterIsInstance<LivingEntity>()
            .firstOrNull { nearby ->
                nearby.type == entity.type &&
                        !nearby.isDead &&
                        nearby.hasMetadata(STACK_METADATA_KEY)
            }
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

        updateStackMetadata(entity, quantity, DEFAULT_DEATH_QUANTITY)
        updateCustomName(entity, quantity, displayName)
    }

    /**
     * Reduz o stack de um mob existente (quando leva dano letal)
     * Incrementa o contador de mortes para multiplicar drops no final
     */
    fun reduceStack(entity: LivingEntity, newStackSize: Int) {
        // Pega o deathQuantity atual e incrementa
        val currentDeathQuantity = entity.getMetadata(DEATH_QUANTITY_KEY)
            .firstOrNull()?.asInt() ?: 0
        val newDeathQuantity = currentDeathQuantity + 1

        updateStackMetadata(entity, newStackSize, newDeathQuantity)
        updateCustomName(entity, newStackSize)
    }

    /**
     * Cria um novo mob stackado a partir de um que morreu (fallback do EntityDeath)
     * Preserva o contador de mortes do mob original
     */
    fun respawnStack(entity: LivingEntity, newStackSize: Int, originalDeathQuantity: Int) {
        val displayName = getEntityName(entity)
        updateStackMetadata(entity, newStackSize, originalDeathQuantity + 1)
        updateCustomName(entity, newStackSize, displayName)
    }

    private fun updateStackMetadata(
        entity: LivingEntity,
        stackSize: Int,
        deathQuantity: Int = DEFAULT_DEATH_QUANTITY
    ) {
        entity.setMetadata(
            STACK_METADATA_KEY,
            FixedMetadataValue(TotalEssentials.getInstance(), stackSize)
        )
        entity.setMetadata(
            DEATH_QUANTITY_KEY,
            FixedMetadataValue(TotalEssentials.getInstance(), deathQuantity)
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