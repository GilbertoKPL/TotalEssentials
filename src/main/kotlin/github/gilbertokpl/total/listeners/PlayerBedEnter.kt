package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent

class PlayerBedEnter : Listener {

    @EventHandler
    fun onPlayerBedEnter(event: PlayerBedEnterEvent) {
        if (!MainConfig.antibugsBlockBed) return
        if (event.player.hasPermission("totalessentials.bypass.bed")) return

        event.player.sendMessage(LangConfig.generalNotPermAction)
        event.isCancelled = true
    }
}