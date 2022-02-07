package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAnnounce : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.announceActivated,
            consoleCanUse = true,
            commandName = "announce",
            timeCoolDown = MainConfig.announceCooldown.toLong(),
            permission = "essentialsk.commands.announce",
            minimumSize = 1,
            maximumSize = null,
            commandUsage = listOf("/announce <msg>", "/anunciar <msg>")
        )

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
            LangConfig.announceSendAnnounce
                .replace("%name%", name)
                .replace("%message%", newMessage)
        )

        return false
    }
}
