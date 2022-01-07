package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAnnounce : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "announce"
    override val timeCoolDown: Long = MainConfig.getInstance().announceCooldown.toLong()
    override val permission: String = "essentialsk.commands.announce"
    override val minimumSize = 1
    override val maximumSize: Nothing? = null
    override val commandUsage = listOf("/announce <msg>", "/anunciar <msg>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val name = if (s !is Player) {
            "Console"
        } else {
            s.name
        }
        val msg = StringBuilder()
        for (arg in args) {
            msg.append(arg).append(" ")
        }
        val newMessage = if (s !is Player) {
            msg.toString()
        } else {
            PermissionUtil.getInstance().colorPermission(s, msg.toString())
        }
        PluginUtil.getInstance().serverMessage(
            GeneralLang.getInstance().announceSendAnnounce
                .replace("%name%", name)
                .replace("%message%", newMessage)
        )
        return false
    }
}
