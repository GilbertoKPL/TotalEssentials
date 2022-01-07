package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.`object`.SpawnData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack


class PlayerTeleport : Listener {
    @EventHandler
    fun event(e: PlayerTeleportEvent) {
        if (MainConfig.getInstance().backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().antibugsBlockGoingEdgeEnderpearl) {
            try {
                blockPassEdgeEnderPearl(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().antibugsBlockPlayerGoToNetherCeiling) {
            try {
                blockPassNetherCeiling(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun setBackLocation(e: PlayerTeleportEvent) {
        if (!e.player.hasPermission("essentialsk.commands.back") || MainConfig.getInstance().backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            ) && !e.player.hasPermission("essentialsk.bypass.backblockedworlds")
        ) return
        DataManager.getInstance().playerCacheV2[e.player.name.lowercase()]?.setBack(e.player.location) ?: return
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
                e.player.sendMessage(GeneralLang.getInstance().generalNotPermAction)
                e.isCancelled = true
            }
        }
    }

    private fun blockPassNetherCeiling(e: PlayerTeleportEvent) {
        if (
            !e.player.hasPermission("essentialsk.bypass.netherceiling") &&
            e.to != null && e.to!!.world!!.environment === World.Environment.NETHER && e.to!!.y > 124.0
        ) {
            val loc = SpawnData("spawn")
            if (loc.checkCache()) {
                e.player.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
                return
            }
            e.player.teleport(loc.getLocation())
            e.player.sendMessage(GeneralLang.getInstance().generalNotPermAction)
        }
    }
}