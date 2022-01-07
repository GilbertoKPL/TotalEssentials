package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.`object`.SpawnData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent


class EntityDamage : Listener {
    @EventHandler
    fun event(e: EntityDamageEvent) {
        if (MainConfig.getInstance().addonsBlockPlayerGoToVoid) {
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
                    p.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
                    return
                }
                p.teleport(loc.getLocation())
            }
        }
    }
}