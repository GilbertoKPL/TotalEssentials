package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommandEvent : Listener {
    @EventHandler
    fun event(e: PlayerCommandPreprocessEvent) {
        try {
            val split = e.message.split(" ")
            vanishPreCommandEvent(e, split)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun vanishPreCommandEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (split.isEmpty()) {
            return
        }
        OtherConfig.getInstance().vanishBlockedOtherCmds.also {
            if (it.containsKey(split[0])) {
                val to = it[split[0]]
                if (split.size >= to!!) {
                    val p = EssentialsK.instance.server.getPlayer(split[to]) ?: return
                    if (PlayerData(p.name).checkVanish()) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }
}