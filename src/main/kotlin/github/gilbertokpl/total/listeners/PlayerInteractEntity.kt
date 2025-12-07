package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class PlayerInteractEntity : Listener {

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (!MainConfig.antibugsBlockNametag) return
        if (event.player.hasPermission("totalessentials.bypass.nametag")) return

        @Suppress("DEPRECATION")
        val itemInHand = event.player.itemInHand

        if (itemInHand.type == Material.NAME_TAG) {
            event.player.sendMessage(LangConfig.generalNotPermAction)
            event.isCancelled = true
        }
    }
}