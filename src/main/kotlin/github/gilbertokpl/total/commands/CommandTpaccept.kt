package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.internal.TpaData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : github.gilbertokpl.core.external.command.CommandCreator("tpaccept") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("back"),
            active = MainConfig.tpaActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/tpaccept")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val p = TpaData.getTpa(s as Player) ?: run {
            s.sendMessage(LangConfig.tpaNotAnyRequest)
            return false
        }

        //check if player is online
        if (github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(p.name) == null) {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        s.sendMessage(
            LangConfig.tpaRequestAccepted.replace(
                "%player%",
                p.name
            )
        )

        val tpaCache = TpaData[p] ?: return false

        tpaCache.otherPlayer = null
        tpaCache.wait = false

        if (p.hasPermission("totalessentials.bypass.teleport")) {
            TpaData.remove(p)
            p.sendMessage(
                LangConfig.tpaRequestOtherNoDelayAccepted.replace(
                    "%player%",
                    s.name
                )
            )
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
