package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpchunk : CommandCreator("tpchunk") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("tpchunk"),
            active = MainConfig.tpActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tp",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf("P_/tpchunk <x> <z>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false
        val player = sender

        val chunkX = args.getOrNull(0)?.toIntOrNull()
        val chunkZ = args.getOrNull(1)?.toIntOrNull()

        if (chunkX == null || chunkZ == null) return true

        val chunk = player.world.getChunkAt(chunkX, chunkZ)
        val block = chunk.getBlock(8, 0, 8) // centro do chunk

        val highestY = player.world.getHighestBlockYAt(block.location).toDouble()
        val tpLocation = Location(player.world, block.x.toDouble(), highestY, block.z.toDouble())

        player.teleportSafe(tpLocation)
        return false
    }
}
