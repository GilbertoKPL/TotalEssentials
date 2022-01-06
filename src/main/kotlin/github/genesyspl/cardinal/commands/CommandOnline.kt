package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.PlayerUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandOnline : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "online"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.online"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/online")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().onlineSendOnline.replace(
                "%amount%",
                PlayerUtil.getInstance()
                    .getIntOnlinePlayers(github.genesyspl.cardinal.configs.MainConfig.getInstance().onlineCountRemoveVanish)
                    .toString()
            )
        )
        return false
    }
}