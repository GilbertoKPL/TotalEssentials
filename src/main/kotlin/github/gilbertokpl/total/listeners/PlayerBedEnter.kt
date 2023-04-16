package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent

class PlayerBedEnter : Listener {
    @EventHandler
    fun event(e: PlayerBedEnterEvent) {
        if (MainConfig.antibugsBlockBed) {
            try {
                blockEnterInBed(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun blockEnterInBed(e: PlayerBedEnterEvent) {
        if (!e.player.hasPermission("totalessentials.bypass.bed")) {
            e.player.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
