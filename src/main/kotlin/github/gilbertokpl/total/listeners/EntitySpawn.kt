package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.stackmobs.StackMobsManager.handleSpawnWithinRange
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class EntitySpawn : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onCreatureSpawn(event: CreatureSpawnEvent) {

        if (!MainConfig.antibugsBlockMobCatch) {
            event.entity.canPickupItems = false
        }

        if (!MainConfig.stackmobsActivated) return

        val entity = event.entity

        // Aplica rotação se for mob respawnado
        applyRespawnRotation(entity)

        // Verifica se o tipo de mob está na lista de stack
        val entityTypeId = getEntityTypeId(entity)
        if (entityTypeId == null || !(MainConfig.stackmobsStackList)?.contains(entityTypeId.toString())!!) return

        makeFireProof(entity)

        handleSpawnWithinRange(entity)
    }

    fun makeFireProof(entity: LivingEntity) {
        try {
            val method = entity.javaClass.methods.firstOrNull { it.name.equals("setFireTicks", true) }
            method?.invoke(entity, 0)
        } catch (_: Exception) {}
    }

    /**
     * Aplica rotação de respawn teleportando a entidade
     */
    private fun applyRespawnRotation(entity: LivingEntity) {
        val yawMeta = entity.getMetadata("respawnYaw")
        val pitchMeta = entity.getMetadata("respawnPitch")

        if (yawMeta.isNotEmpty() && pitchMeta.isNotEmpty()) {
            try {
                val yaw = (yawMeta[0].value() as Number).toFloat()
                val pitch = (pitchMeta[0].value() as Number).toFloat()

                val location = entity.location.clone()
                location.yaw = yaw
                location.pitch = pitch

                entity.teleport(location)

                // Remove metadatas após usar
                entity.removeMetadata("respawnYaw", github.gilbertokpl.total.TotalEssentials.getInstance())
                entity.removeMetadata("respawnPitch", github.gilbertokpl.total.TotalEssentials.getInstance())
            } catch (_: Exception) {}
        }
    }

    /**
     * Obtém o typeId de forma compatível com versões antigas
     */
    private fun getEntityTypeId(entity: LivingEntity): Int? {
        return try {
            @Suppress("DEPRECATION")
            entity.type.typeId.toInt()
        } catch (_: Exception) {
            try {
                val method = entity.type.javaClass.getMethod("getTypeId")
                (method.invoke(entity.type) as Number).toInt()
            } catch (_: Exception) {
                null
            }
        }
    }
}