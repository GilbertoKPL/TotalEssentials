package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeaveEvent : Listener {
    @EventHandler
    fun event(e: PlayerQuitEvent) {
        if (MainConfig.getInstance().backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            e.quitMessage = null
            PluginUtil.getInstance().serverMessage(
                GeneralLang.getInstance().messagesLeaveMessage
                    .replace("%player%", e.player.name)
            )
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun setBackLocation(e: PlayerQuitEvent) {
        if (!e.player.hasPermission("essentialsk.commands.back") || MainConfig.getInstance().backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
        Dao.getInstance().backLocation[e.player] = e.player.location
    }
}