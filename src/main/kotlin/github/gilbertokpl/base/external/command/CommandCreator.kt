package github.gilbertokpl.base.external.command

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.external.command.interfaces.CommandBase
import github.gilbertokpl.base.internal.command.InternalCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class CommandCreator(name: String) : Command(name), CommandBase {

    private var ic: InternalCommand = InternalCommand(this)

    val hashCountDown = HashMap<Player, Long>()

    var basePlugin: BasePlugin? = null
    var active = true
    var target = CommandTarget.ALL
    var commandUsage = emptyList<String>()
    var countdown: Long? = 0L
    var minimumSize: Int? = 0
    var maximumSize: Int? = 0

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return ic.execute(sender, commandLabel, args)
    }
}