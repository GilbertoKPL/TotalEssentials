package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*

class PlayerPreCommandEvent : Listener {
    @EventHandler
    fun event(e: PlayerCommandPreprocessEvent) {
        val split = e.message.split(" ")
        if (MainConfig.getInstance().vanishActivated) {
            try {
                vanishPreCommandEvent(e, split)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            blockCommands(e, split)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }

    fun blockCommands(e: PlayerCommandPreprocessEvent, split: List<String>) {
        val cmd = split[0]
        for (blockedCmd in MainConfig.getInstance().antibugsBlockCmds) {
            if ((blockedCmd == cmd || cmd.split(":").toTypedArray().size > 1 && blockedCmd == "/" + cmd.split(":")
                    .toTypedArray()[1]) &&
                !e.player.hasPermission("essentialsk.bypass.blockedcmd")
            ) {
                e.player.sendMessage(GeneralLang.getInstance().generalNotPerm)
                e.isCancelled = true
                return
            }
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