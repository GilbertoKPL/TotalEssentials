package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFeed : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.feed"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/feed")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if ((s as Player).foodLevel >= 15 && MainConfig.getInstance().feedNeedEatBelow) {
            s.sendMessage(GeneralLang.getInstance().feedSendFullMessage)
            return false
        }
        s.foodLevel = 15
        s.sendMessage(GeneralLang.getInstance().feedSendMessage)
        return true
    }
}