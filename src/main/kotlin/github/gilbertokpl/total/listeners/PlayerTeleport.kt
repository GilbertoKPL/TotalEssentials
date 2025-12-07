package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack

class PlayerTeleport : Listener {

    companion object {
        private const val NETHER_CEILING_Y = 124.0
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (MainConfig.backActivated) {
            setBackLocation(event)
        }

        if (MainConfig.antibugsBlockGoingEdgeEnderpearl) {
            blockPassEdgeEnderPearl(event)
        }

        if (MainConfig.antibugsBlockPlayerGoToNetherCeiling) {
            blockPassNetherCeiling(event)
        }
    }

    private fun setBackLocation(event: PlayerTeleportEvent) {
        val player = event.player

        val hasPermission = player.hasPermission("totalessentials.commands.back")
        val canBypassBlockedWorlds = player.hasPermission("totalessentials.bypass.backblockedworlds")
        val isWorldBlocked = MainConfig.backDisabledWorlds.contains(player.world.name.lowercase())

        if (!hasPermission || (isWorldBlocked && !canBypassBlockedWorlds)) return
        if (player.location == SpawnData.spawnLocation["spawn"]) return

        PlayerData.backLocation[player] = player.location
    }

    private fun blockPassEdgeEnderPearl(event: PlayerTeleportEvent) {
        if (event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return

        val destination = event.to ?: return
        val player = event.player

        // Usa reflection para evitar carregar WorldBorder em versões antigas
        if (isOutsideWorldBorderReflection(destination.x, destination.z, player.world)) {
            player.inventory.addItem(ItemStack(Material.ENDER_PEARL))
            player.sendMessage(LangConfig.generalNotPermAction)
            event.isCancelled = true
        }
    }

    private fun blockPassNetherCeiling(event: PlayerTeleportEvent) {
        val player = event.player
        val destination = event.to ?: return

        if (player.hasPermission("totalessentials.bypass.netherceiling")) return

        val isNether = try {
            destination.world?.environment == World.Environment.NETHER
        } catch (e: Exception) {
            destination.world?.name?.lowercase()?.contains("nether") == true
        }

        if (!isNether) return
        if (destination.y <= NETHER_CEILING_Y) return

        SpawnData.teleportToSpawn(player)
        player.sendMessage(LangConfig.generalNotPermAction)
    }

    private fun isOutsideWorldBorderReflection(x: Double, z: Double, world: org.bukkit.World): Boolean {
        try {
            // Tenta obter WorldBorder via reflection
            val getWorldBorderMethod = world.javaClass.getMethod("getWorldBorder")
            val worldBorder = getWorldBorderMethod.invoke(world) ?: return false

            // Obtém o tamanho da borda
            val getSizeMethod = worldBorder.javaClass.getMethod("getSize")
            val size = getSizeMethod.invoke(worldBorder) as Double
            val radius = size / 2.0

            // Obtém o centro
            val getCenterMethod = worldBorder.javaClass.getMethod("getCenter")
            val center = getCenterMethod.invoke(worldBorder) as org.bukkit.Location

            return x > center.x + radius ||
                    x < center.x - radius ||
                    z > center.z + radius ||
                    z < center.z - radius
        } catch (e: Exception) {
            // WorldBorder não disponível, não verifica
            return false
        }
    }
}