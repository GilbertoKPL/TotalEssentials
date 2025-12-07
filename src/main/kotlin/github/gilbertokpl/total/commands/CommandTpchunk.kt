package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpchunk : CommandManager("tpchunk") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(),
            active = MainConfig.tpActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tp",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf("P_/tpchunk <x> <z>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false

        val chunkX = args.getOrNull(0)?.toIntOrNull()
        val chunkZ = args.getOrNull(1)?.toIntOrNull()

        if (chunkX == null || chunkZ == null) return true

        val chunk = sender.world.getChunkAt(chunkX, chunkZ)
        val block = chunk.getBlock(8, 0, 8) // centro do chunk

        val highestY = sender.world.getHighestBlockYAt(block.location).toDouble()
        val tpLocation = Location(sender.world, block.x.toDouble(), highestY, block.z.toDouble())

        sender.teleportSafe(tpLocation)
        return false
    }
}
