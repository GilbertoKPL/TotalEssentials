package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.SpawnData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathEvent : Listener {
    @EventHandler
    fun event(e: PlayerDeathEvent) {
        if (MainConfig.getInstance().backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().spawnActivated) {
            try {
                spawnDeath(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun setBackLocation(e: PlayerDeathEvent) {
        if (!e.entity.hasPermission("essentialsk.commands.back") || MainConfig.getInstance().backDisabledWorlds.contains(
                e.entity.world.name.lowercase()
            )
        ) return
        Dao.getInstance().backLocation[e.entity] = e.entity.location
    }

    private fun spawnDeath(e: PlayerDeathEvent) {
        val loc = SpawnData("spawn")
        if (loc.checkCache()) {
            e.entity.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.entity.teleport(SpawnData("spawn").getLocation())
    }
}