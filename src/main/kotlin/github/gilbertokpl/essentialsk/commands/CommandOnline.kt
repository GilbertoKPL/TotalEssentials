package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandOnline : CommandCreator {
    override val active: Boolean = MainConfig.onlineActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "online"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.online"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/online")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        s.sendMessage(
            GeneralLang.onlineSendOnline.replace(
                "%amount%",
                PlayerUtil.getIntOnlinePlayers(MainConfig.onlineCountRemoveVanish)
                    .toString()
            )
        )
        return false
    }
}
