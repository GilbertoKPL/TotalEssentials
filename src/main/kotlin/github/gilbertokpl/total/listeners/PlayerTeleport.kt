package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack

class PlayerTeleport : Listener {
    @EventHandler
    fun event(e: PlayerTeleportEvent) {
        if (MainConfig.backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Throwable) {

            }
        }
        if (MainConfig.antibugsBlockGoingEdgeEnderpearl) {
            try {
                blockPassEdgeEnderPearl(e)
            } catch (e: Throwable) {

            }
        }
        if (MainConfig.antibugsBlockPlayerGoToNetherCeiling) {
            try {
                blockPassNetherCeiling(e)
            } catch (e: Throwable) {

            }
        }
    }


    private fun setBackLocation(e: PlayerTeleportEvent) {
        if (!e.player.hasPermission("totalessentials.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            ) && !e.player.hasPermission("totalessentials.bypass.backblockedworlds")
        ) return
        PlayerData.backLocation[e.player] = e.player.location
    }

    private fun blockPassEdgeEnderPearl(e: PlayerTeleportEvent) {
        if (
            e.cause === PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
            e.to != null
        ) {
            val p = e.player
            val wB = p.world.worldBorder.size / 2.0
            val center = p.world.worldBorder.center
            val to = e.to!!
            if (center.x + wB < to.x || center.x - wB > to.x || center.z + wB < to.z || center.z - wB > to.z) {
                p.inventory.addItem(ItemStack(Material.ENDER_PEARL, 1))
                e.player.sendMessage(LangConfig.generalNotPermAction)
                e.isCancelled = true
            }
        }
    }

    private fun blockPassNetherCeiling(e: PlayerTeleportEvent) {
        val p = e.player
        if (
            !p.hasPermission("totalessentials.bypass.netherceiling") &&
            e.to != null && e.to!!.world!!.environment === World.Environment.NETHER && e.to!!.y > 124.0
        ) {
            SpawnData.teleport(p)
            e.player.sendMessage(LangConfig.generalNotPermAction)
        }
    }
}
