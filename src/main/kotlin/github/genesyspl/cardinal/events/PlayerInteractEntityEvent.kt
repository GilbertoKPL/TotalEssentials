package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class PlayerInteractEntityEvent : Listener {
    @EventHandler
    fun event(e: PlayerInteractEntityEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().antibugsBlockNametag) {
            try {
                blockNameTag(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockNameTag(e: PlayerInteractEntityEvent) {
        @Suppress("DEPRECATION")
        if (e.player.itemInHand.type == Material.NAME_TAG &&
            !e.player.hasPermission("cardinal.bypass.nametag")
        ) {
            e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
        }
    }
}