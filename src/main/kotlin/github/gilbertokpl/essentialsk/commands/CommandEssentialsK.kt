package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.ConfigUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandEssentialsK : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "essentialsk"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.essentialsk"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/essentialsk reload")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args[0] == "reload") {
            ConfigUtil.getInstance().reloadConfig(true)
            return false
        }
        return true
    }
}