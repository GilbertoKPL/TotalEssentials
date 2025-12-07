package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorld : Listener {

    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        if (!MainConfig.flyActivated) return

        val player = event.player
        val task = TotalEssentials.getCore().getTask()

        task.async {
            task.waitSeconds(1)

            task.sync {
                restoreGameMode(player)
                handleFlyPermission(player)
            }
        }
    }

    private fun restoreGameMode(player: Player) {
        val savedGameMode = PlayerData.gameModeCache[player] ?: return
        val gameMode = PlayerUtil.getGameModeFromString(savedGameMode.toString())

        if (player.gameMode != gameMode) {
            player.gameMode = gameMode
        }
    }

    private fun handleFlyPermission(player: Player) {
        val hasFlyEnabled = PlayerData.flyCache[player] ?: false
        val isInSurvival = PlayerData.gameModeCache[player] == 0

        if (!hasFlyEnabled || !isInSurvival) return

        val currentWorld = player.world.name
        val isWorldBlocked = MainConfig.flyDisabledWorlds.contains(currentWorld)

        if (isWorldBlocked) {
            disableFly(player)
        } else {
            enableFly(player)
        }
    }

    private fun enableFly(player: Player) {
        player.allowFlight = true
        player.isFlying = true
    }

    private fun disableFly(player: Player) {
        player.sendMessage(LangConfig.flyDisabledWorld)
        player.allowFlight = false
        player.isFlying = false
    }
}