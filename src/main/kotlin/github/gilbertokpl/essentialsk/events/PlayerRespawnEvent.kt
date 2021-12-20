package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.SpawnData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawnEvent : Listener {
    @EventHandler
    fun event(e: PlayerRespawnEvent) {
        if (MainConfig.getInstance().spawnActivated) {
            try {
                spawnRespawn(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun spawnRespawn(e: PlayerRespawnEvent) {
        val loc = SpawnData("spawn")
        if (loc.checkCache()) {
            e.player.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.player.teleport(SpawnData("spawn").getLocation())
    }
}