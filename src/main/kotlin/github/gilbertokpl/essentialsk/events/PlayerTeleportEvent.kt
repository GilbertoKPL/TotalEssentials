package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack


class PlayerTeleportEvent : Listener {
    @EventHandler
    fun event(e: PlayerTeleportEvent) {
        if (MainConfig.getInstance().backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().antibugsBlockGoingEdgeEnderearl) {
            try {
                blockPassEdgeEnderPearl(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().antibugsPlayerGoToNetherCeiling) {
            try {
                blockPassNetherCeiling(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun setBackLocation(e: PlayerTeleportEvent) {
        if (!e.player.hasPermission("essentialsk.commands.back") || MainConfig.getInstance().backDisabledWorlds.contains(e.player.world.name.lowercase())) return
        Dao.getInstance().backLocation[e.player] = e.player.location
    }

    private fun blockPassEdgeEnderPearl(e: PlayerTeleportEvent) {
        if (
            e.cause === PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
            e.to != null) {
            val p = e.player
            val wB = p.world.worldBorder.size / 2.0
            val center  = p.world.worldBorder.center
            val to  = e.to!!
            if (center.x + wB < to.x || center.x - wB > to.x || center.z + wB < to.z || center.z - wB > to.z) {
                p.inventory.addItem(ItemStack(Material.ENDER_PEARL, 1))
                e.isCancelled = true
            }
        }
    }

    private fun blockPassNetherCeiling(e: PlayerTeleportEvent) {
        if (
            !e.player.hasPermission("essentialsk.resources.bypass.netherceiling") &&
            e.to != null && e.to!!.world!!.environment === World.Environment.NETHER && e.to!!.y > 124.0
        ) {

        }
    }
}