package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.`object`.SpawnData
import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent


class EntityDamageEvent : Listener {
    @EventHandler
    fun event(e: EntityDamageEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsBlockPlayerGoToVoid) {
            try {
                blockPlayerFallInVoid(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockPlayerFallInVoid(e: EntityDamageEvent) {
        if (e.entity is Player && e.cause == EntityDamageEvent.DamageCause.VOID) {
            val p = e.entity as Player
            if (p.location.blockY < 0) {
                e.isCancelled = true
                p.fallDistance = 1.0f
                val loc = SpawnData("spawn")
                if (loc.checkCache()) {
                    p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendNotSet)
                    return
                }
                p.teleport(loc.getLocation())
            }
        }
    }
}