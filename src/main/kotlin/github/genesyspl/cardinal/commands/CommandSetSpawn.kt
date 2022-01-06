package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.`object`.SpawnData
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetSpawn : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "setspawn"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.setspawn"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/setspawn")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        SpawnData("spawn").setSpawn((s as Player).location, s)
        return false
    }
}