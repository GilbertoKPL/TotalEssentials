package github.gilbertokpl.core.external.command

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.command.interfaces.CommandBase
import github.gilbertokpl.core.internal.command.InternalCommand
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

abstract class CommandCreator(name: String) : BukkitCommand(name), CommandBase {

    private val internalCommand: InternalCommand = InternalCommand(this)

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