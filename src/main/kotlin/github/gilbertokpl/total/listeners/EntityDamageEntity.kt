package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.stackmobs.StackMobsManager
import github.gilbertokpl.total.stackmobs.StackMobsManager.mobCreate
import org.bukkit.entity.Damageable
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.metadata.FixedMetadataValue

class EntityDamageEntity : Listener {

    companion object {
        private const val STACK_METADATA_KEY = "stack"
        private const val STACK_PROCESSED_KEY = "StackProcessed"
        private const val STACK_PROCESSING_KEY = "StackProcessing"

        private val STACK_PROTECTED_CAUSES: Set<EntityDamageEvent.DamageCause> by lazy {
            buildSet {
                addAll(listOf(
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.FIRE_TICK,
                    EntityDamageEvent.DamageCause.LAVA,
                    EntityDamageEvent.DamageCause.SUFFOCATION,
                    EntityDamageEvent.DamageCause.DROWNING,
                    EntityDamageEvent.DamageCause.CONTACT,
                    EntityDamageEvent.DamageCause.MAGIC,
                    EntityDamageEvent.DamageCause.VOID,
                    EntityDamageEvent.DamageCause.STARVATION,
                    EntityDamageEvent.DamageCause.FALL
                ))
                tryAddCause("WITHER")
                tryAddCause("POISON")
            }
        }

        private fun MutableSet<EntityDamageEvent.DamageCause>.tryAddCause(name: String) {
            try {
                add(EntityDamageEvent.DamageCause.valueOf(name))
            } catch (_: IllegalArgumentException) {}
        }
    }

    private val plugin = TotalEssentials.getInstance()
    private val compat = ReflectionCompat()

    // ────────────────────────────────────────────────────────────────
    //  MOB STACKADO ATACA PLAYER → MULTIPLICA DANO
    // ────────────────────────────────────────────────────────────────
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {

        if (MainConfig.addonsBlockExplodeItems && event.entity is Item) {
            event.isCancelled = true
            return
        }

        if (!MainConfig.stackmobsActivated) return

        val damaged = event.entity
        val damager = event.damager as? LivingEntity ?: return
        if (damager is Player) return

        // Mob stackado atacando player → multiplica dano
        if (damaged is Player) {
            val stack = damager.getStackSize() ?: return
            val dmg = compat.getDamage(event)
            compat.setDamage(event, dmg * stack)
            return
        }

        // Mob stackado levando dano
        if (damaged is LivingEntity && damaged !is Player) {
            val stack = damaged.getStackSize() ?: return
            val dmg = compat.getDamage(event)
            handleStackDamage(damaged, dmg, stack, event)
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  OUTROS TIPOS DE DANO (sol, fogo, queda…)
    // ────────────────────────────────────────────────────────────────
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) return
        if (!MainConfig.stackmobsActivated) return
        if (event.cause !in STACK_PROTECTED_CAUSES) return

        val entity = event.entity as? LivingEntity ?: return
        if (entity is Player) return

        val stack = entity.getStackSize() ?: return
        val dmg = compat.getDamage(event)

        handleStackDamage(entity, dmg, stack, event)
    }

    // ────────────────────────────────────────────────────────────────
    //  PROCESSA DANO LETAL EM MOB STACKADO
    // ────────────────────────────────────────────────────────────────
    private fun handleStackDamage(
        entity: LivingEntity,
        damage: Double,
        stack: Int,
        event: EntityDamageEvent
    ) {
        // Último do stack, deixa morrer normal
        if (stack <= 1) return

        // Verifica se entidade ainda é válida
        if (!entity.isValid || entity.isDead) return

        // Evita processamento duplo (race condition)
        if (entity.hasMetadata(STACK_PROCESSING_KEY)) return

        val currentHP = compat.getHealth(entity)

        // Dano não letal, deixa passar
        if (damage < currentHP) return

        // CANCELA IMEDIATAMENTE antes de qualquer coisa
        event.isCancelled = true

        // Marca que está processando (lock)
        entity.setMetadata(STACK_PROCESSING_KEY, FixedMetadataValue(plugin, true))

        try {
            // Verifica novamente após o lock
            if (!entity.isValid || entity.isDead) return

            // Marca como processado para o EntityDeath saber
            entity.setMetadata(STACK_PROCESSED_KEY, FixedMetadataValue(plugin, true))

            // Salva rotação atual
            val location = entity.location
            entity.setMetadata("respawnYaw", FixedMetadataValue(plugin, location.yaw))
            entity.setMetadata("respawnPitch", FixedMetadataValue(plugin, location.pitch))

            // Reseta vida
            compat.resetHealth(entity)

            // Respawna com stack - 1
            val remain = stack - 1
            StackMobsManager.reduceStack(entity, remain)

        } finally {
            entity.removeMetadata(STACK_PROCESSING_KEY, plugin)
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  EXTENSION FUNCTION
    // ────────────────────────────────────────────────────────────────
    private fun LivingEntity.getStackSize(): Int? {
        if (!hasMetadata(STACK_METADATA_KEY)) return null
        return getMetadata(STACK_METADATA_KEY).firstOrNull()?.asInt()
    }

    // ────────────────────────────────────────────────────────────────
    //  COMPATIBILIDADE COM 1.5.2 → 1.21+
    // ────────────────────────────────────────────────────────────────
    private class ReflectionCompat {

        private val healthGetter = resolveMethod(Damageable::class.java, "getHealth")
        private val healthSetter = resolveMethodByName(Damageable::class.java, "setHealth")
        private val maxHealthGetter = resolveMethod(Damageable::class.java, "getMaxHealth")

        private val damageGetter = resolveMethod(EntityDamageEvent::class.java, "getDamage")
        private val damageSetter = resolveMethodByName(EntityDamageEvent::class.java, "setDamage")

        private val damageField = resolveFieldHierarchy(EntityDamageEvent::class.java, "damage")

        private fun resolveMethod(clazz: Class<*>, name: String): java.lang.reflect.Method? {
            return try {
                clazz.getMethod(name).apply { isAccessible = true }
            } catch (_: Exception) { null }
        }

        private fun resolveMethodByName(clazz: Class<*>, name: String): java.lang.reflect.Method? {
            return try {
                clazz.methods.firstOrNull {
                    it.name == name && it.parameterCount == 1
                }?.apply { isAccessible = true }
            } catch (_: Exception) { null }
        }

        private fun resolveFieldHierarchy(clazz: Class<*>, name: String): java.lang.reflect.Field? {
            var current: Class<*>? = clazz
            while (current != null) {
                try {
                    return current.getDeclaredField(name).apply { isAccessible = true }
                } catch (_: NoSuchFieldException) {
                    current = current.superclass
                } catch (_: Exception) {
                    return null
                }
            }
            return null
        }

        private fun isIntParam(method: java.lang.reflect.Method): Boolean {
            val param = method.parameterTypes[0]
            return param == Int::class.javaPrimitiveType || param == Int::class.java
        }

        fun getHealth(entity: LivingEntity): Double {
            healthGetter?.let { method ->
                try {
                    return (method.invoke(entity) as Number).toDouble()
                } catch (_: Exception) {}
            }
            return entity.health
        }

        fun getMaxHealth(entity: LivingEntity): Double {
            maxHealthGetter?.let { method ->
                try {
                    return (method.invoke(entity) as Number).toDouble()
                } catch (_: Exception) {}
            }
            return entity.maxHealth
        }

        fun resetHealth(entity: LivingEntity) {
            val max = getMaxHealth(entity)
            healthSetter?.let { method ->
                try {
                    val value: Any = if (isIntParam(method)) max.toInt() else max
                    method.invoke(entity, value)
                    return
                } catch (_: Exception) {}
            }
            entity.health = max
        }

        fun getDamage(event: EntityDamageEvent): Double {
            damageGetter?.let { method ->
                try {
                    return (method.invoke(event) as Number).toDouble()
                } catch (_: Exception) {}
            }
            damageField?.let { field ->
                try {
                    return (field.get(event) as Number).toDouble()
                } catch (_: Exception) {}
            }
            return event.damage
        }

        fun setDamage(event: EntityDamageEvent, dmg: Double) {
            damageSetter?.let { method ->
                try {
                    val value: Any = if (isIntParam(method)) dmg.toInt() else dmg
                    method.invoke(event, value)
                    return
                } catch (_: Exception) {}
            }
            damageField?.let { field ->
                try {
                    val fieldType = field.type
                    val value: Any = if (fieldType == Int::class.javaPrimitiveType || fieldType == Int::class.java) {
                        dmg.toInt()
                    } else {
                        dmg
                    }
                    field.set(event, value)
                    return
                } catch (_: Exception) {}
            }
            event.damage = dmg
        }
    }
}