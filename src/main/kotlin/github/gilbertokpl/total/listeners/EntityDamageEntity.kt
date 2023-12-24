package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.StackMobsUtil.mobCreate
import org.bukkit.entity.Damageable
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.lang.reflect.Field
import java.lang.reflect.Method

class EntityDamageEntity : Listener {

    private var error = false

    @EventHandler
    fun event(e: EntityDamageByEntityEvent) {
        try {
            if (MainConfig.addonsBlockExplodeItems) {
                blockItemsExplode(e)
            }

            if (MainConfig.stackmobsActivated) {
                stackMobsEvent(e)
            }
        } catch (ex: Exception) {
            // Log the exception or handle it appropriately
            ex.printStackTrace()
        }
    }

    private fun blockItemsExplode(e: EntityDamageByEntityEvent) {
        if (e.entity is Item) {
            e.isCancelled = true
        }
    }

    private fun stackMobsEvent(e: EntityDamageByEntityEvent) {
        val damager = e.damager
        val damagedEntity = e.entity

        if (damagedEntity is Player || damagedEntity !is LivingEntity || damager !is Player) {
            return
        }

        if (damagedEntity.hasMetadata("stack")) {
            handleStackedMobs(damagedEntity, e)
        }
    }

    private fun handleStackedMobs(entity: LivingEntity, e: EntityDamageByEntityEvent) {
        val quantity = entity.getMetadata("stack")[0].asInt() - 1

        if (quantity == 0) {
            return
        }

        val (damage, entityHealth) = calculateDamageAndHealth(entity, e)

        if (damage >= entityHealth) {
            e.isCancelled = true
            resetEntityHealth(entity)
            mobCreate(entity, quantity, entity.getMetadata("DeathQuantity")[0].asInt() + 1)
        }
    }

    private fun calculateDamageAndHealth(entity: LivingEntity, e: EntityDamageByEntityEvent): Pair<Double, Double> {
        var damage: Double
        var entityHealth: Double

        if (!error) {
            try {
                val method: Method = Damageable::class.java.getDeclaredMethod("getHealth")
                method.isAccessible = true
                entityHealth = (method.invoke(entity) as Int).toDouble()

                val field: Field = EntityDamageEvent::class.java.getDeclaredField("damage")
                field.isAccessible = true
                damage = field.getDouble(e)
                println(damage)
            } catch (ex: NoSuchFieldException) {
                damage = e.damage
                entityHealth = entity.health
                error = true
            }
        } else {
            damage = e.damage
            entityHealth = entity.health
        }

        return damage to entityHealth
    }

    private fun resetEntityHealth(entity: LivingEntity) {
        if (!error) {
            try {
                val method: Method = Damageable::class.java.getDeclaredMethod("getMaxHealth")
                method.isAccessible = true

                val maxHealth = (method.invoke(entity) as Int)

                println(maxHealth)

                val method1: Method = Damageable::class.java.getDeclaredMethod("setHealth", Int::class.javaPrimitiveType)
                method1.isAccessible = true
                method1.invoke(entity, maxHealth)
            } catch (ex: Exception) {
                // Log the exception or handle it appropriately
                ex.printStackTrace()
            }
        } else {
            entity.health = entity.maxHealth
        }
    }
}