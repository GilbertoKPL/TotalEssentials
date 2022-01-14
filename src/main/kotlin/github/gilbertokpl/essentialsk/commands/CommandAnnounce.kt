package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAnnounce : CommandCreator {

    override val active: Boolean = MainConfig.announceActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "announce"
    override val timeCoolDown: Long = MainConfig.announceCooldown.toLong()
    override val permission: String = "essentialsk.commands.announce"
    override val minimumSize = 1
    override val maximumSize: Nothing? = null
    override val commandUsage = listOf("/announce <msg>", "/anunciar <msg>")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val name = if (s !is Player) {
            "Console"
        } else {
            s.name
        }

        val msg = StringBuilder()
        for (arg in args) {
            msg.append(arg).append(" ")
        }

        val p = if (s is Player) {
            s
        } else {
            null
        }

        val newMessage = PermissionUtil.colorPermission(p, msg.toString())

        MainUtil.serverMessage(
            GeneralLang.announceSendAnnounce
                .replace("%name%", name)
                .replace("%message%", newMessage)
        )

        return false
    }
}
