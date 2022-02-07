package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.TpaData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.tpaActivated,
            consoleCanUse = false,
            commandName = "tpaccept",
            timeCoolDown = null,
            permission = "essentialsk.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/tpaccept")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = TpaData.getTpa(s as Player) ?: run {
            s.sendMessage(LangConfig.tpaNotAnyRequest)
            return false
        }

        //check if player is online
        if (EssentialsK.instance.server.getPlayer(p.name) == null) {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        s.sendMessage(LangConfig.tpaRequestAccepted.replace("%player%", p.name))

        val tpaCache = TpaData[p] ?: return false

        tpaCache.otherPlayer = null
        tpaCache.wait = false

        if (p.hasPermission("essentialsk.bypass.teleport")) {
            TpaData.remove(p)
            p.sendMessage(LangConfig.tpaRequestOtherNoDelayAccepted.replace("%player%", s.name))
            p.teleport(s.location)
            return false
        }

        val time = MainConfig.tpaTimeToTeleport

        p.sendMessage(
            LangConfig.tpaRequestOtherAccepted.replace("%player%", s.name)
                .replace("%time%", time.toString())
        )

        val exe = TaskUtil.teleportExecutor(time)

        exe {
            TpaData.remove(p)
            p.teleport(s.location)
        }

        return false
    }
}
