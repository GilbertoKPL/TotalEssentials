package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentialsJava.instance
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

object StackMobsUtil {
    fun handleSpawnWithinRange(ent: LivingEntity) {

        for (worlds in MainConfig.stackmobsBlockedWorlds) {
            if (ent.world.name.equals(worlds, ignoreCase = true)) {
                return
            }
        }

        val nearbyEntities = ent.getNearbyEntities(
            MainConfig.stackmobsRadius.toDouble(),
            MainConfig.stackmobsRadius.toDouble(),
            MainConfig.stackmobsRadius.toDouble()
        )

        for (entity in nearbyEntities) {
            if (entity is LivingEntity && entity.type == ent.type && !entity.isDead && entity.hasMetadata("stack")) {
                stackMobsWithinRange(entity, ent)
                return
            }
        }

        if (!ent.hasMetadata("stack")) {
            handleNonStackedSpawn(ent)
        }

    }

    private fun stackMobsWithinRange(target: LivingEntity, source: LivingEntity) {
        val stackSize = target.getMetadata("stack")[0].asInt() + 1

        if (stackSize > MainConfig.stackmobsMax) return

        val nome = MainConfig.stackmobsNameTag.replace("%name%", getEntityName(target))
            .replace("%quantity%", stackSize.toString())
        target.setMetadata("stack", FixedMetadataValue(instance, stackSize))
        target.customName = nome
        target.isCustomNameVisible = true

        source.remove()
    }

    private fun handleNonStackedSpawn(ent: LivingEntity) {
        mobCreate(ent, 1, getEntityName(ent))
    }

    fun mobCreate(entity: Entity, quantity: Int, name: String) {
        if (entity !is LivingEntity) return

        entity.setMetadata("stack", FixedMetadataValue(instance, quantity))
        entity.setMetadata("DeathQuantity", FixedMetadataValue(instance, 1))

        val customName = MainConfig.stackmobsNameTag.replace("%name%", name).replace("%quantity%", quantity.toString())

        entity.customName = customName
        entity.isCustomNameVisible = true
    }

    fun mobCreate(entity: Entity, quantity: Int, deathQuantity: Int) {
        if (entity !is LivingEntity) return

        val customName = MainConfig.stackmobsNameTag.replace("%name%", getEntityName(entity))
            .replace("%quantity%", quantity.toString())

        entity.setMetadata("stack", FixedMetadataValue(instance, quantity))
        entity.setMetadata("DeathQuantity", FixedMetadataValue(instance, deathQuantity))

        entity.customName = customName
        entity.isCustomNameVisible = true
    }

    private fun getEntityName(entity: LivingEntity): String {
        return MainConfig.stackmobsNameReplacer.firstOrNull { it.startsWith("${entity.type.typeId.toInt()}:") }
            ?.split(":")?.get(1)?.replace("&", "§")
            ?: entity.toString().replace("Craft", "")
    }
}