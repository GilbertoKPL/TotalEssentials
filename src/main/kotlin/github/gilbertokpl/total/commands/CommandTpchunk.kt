package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpchunk : github.gilbertokpl.core.external.command.CommandCreator("tpchunk") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("tpchunk"),
            active = MainConfig.tpActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tp",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf(
                "P_/tpchunk x z",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        try {
            args[0].toInt()
            args[1].toInt()
        } catch (ex: Exception) {
            return true
        }

        val chunk = p.world.getChunkAt(args[0].toInt(), args[1].toInt())
        val b = chunk.getBlock(8, 0, 8)
        val x: Double = b.x.toDouble()
        val y: Double = b.y.toDouble()
        val z: Double = b.z.toDouble()
        p.teleport(Location(p.world, x, p.world.getHighestBlockYAt(Location(p.world, x, y, z)).toDouble(), z))

        return false
    }
}
