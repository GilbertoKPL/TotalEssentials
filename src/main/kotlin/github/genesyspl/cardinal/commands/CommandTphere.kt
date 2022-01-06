package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTphere : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "tphere"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.tphere"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/tphere <playerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // check if player is online
        val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        p.teleport((s as Player).location)

        p.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().tphereTeleportedOtherSuccess
        )
        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().tphereTeleportedSuccess.replace(
                "%player%",
                p.name
            )
        )
        return false
    }
}