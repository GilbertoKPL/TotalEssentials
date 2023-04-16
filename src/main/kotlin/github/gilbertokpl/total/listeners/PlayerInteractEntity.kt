package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class PlayerInteractEntity : Listener {
    @EventHandler
    fun event(e: PlayerInteractEntityEvent) {
        if (MainConfig.antibugsBlockNametag) {
            try {
                blockNameTag(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun blockNameTag(e: PlayerInteractEntityEvent) {
        @Suppress("DEPRECATION")
        if (e.player.itemInHand.type == Material.NAME_TAG &&
            !e.player.hasPermission("totalessentials.bypass.nametag")
        ) {
            e.player.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
        }
    }
}
