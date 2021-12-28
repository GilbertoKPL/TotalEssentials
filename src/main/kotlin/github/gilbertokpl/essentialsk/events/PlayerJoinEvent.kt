package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.data.SpawnData
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.*
import net.dv8tion.jda.api.EmbedBuilder
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerJoinEvent) {
        e.joinMessage = null
        if (MainConfig.getInstance().spawnSendToSpawnOnLogin) {
            try {
                spawnLogin(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            PlayerData(e.player.name).loadCache()
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
        if (MainConfig.getInstance().vanishActivated) {
            try {
                vanishLoginEvent(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun vanishLoginEvent(e: PlayerJoinEvent) {
        if (e.player.hasPermission("essentialsk.commands.vanish") || e.player.hasPermission("essentialsk.bypass.vanish")) return
        ReflectUtil.getInstance().getPlayers().forEach {
            if (PlayerData(it.name).checkVanish()) {
                @Suppress("DEPRECATION")
                e.player.hidePlayer(it)
            }
        }
    }

    private fun spawnLogin(e: PlayerJoinEvent) {
        val loc = SpawnData("spawn")
        if (loc.checkCache()) {
            e.player.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
            return
        }
        e.player.teleport(SpawnData("spawn").getLocation())
    }
}