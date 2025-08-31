package github.gilbertokpl.core.command.external

import github.gilbertokpl.core.CorePlugin
import github.gilbertokpl.core.command.CoreCommand
import github.gilbertokpl.core.command.interfaces.CommandBase
import github.gilbertokpl.core.command.interfaces.CommandTarget
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

abstract class CommandCreator(name: String) : BukkitCommand(name), CommandBase {

    private val internalCommand: CoreCommand = CoreCommand(this)

    val hashCountDown: HashMap<Player, Long> = HashMap()

    var basePlugin: CorePlugin? = null
    var active = true
    var target = CommandTarget.ALL
    var commandUsage = emptyList<String>()
    var countdown: Long? = 0L
    var minimumSize: Int? = 0
    var maximumSize: Int? = 0

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return internalCommand.execute(sender, commandLabel, args)
    }
}