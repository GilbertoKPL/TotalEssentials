package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.core.external.task.SynchronizationContext
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataTeleport
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
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

        val p = DataTeleport.getTpa(s as Player) ?: run {
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

        val tpaCache = DataTeleport[p] ?: return false

        tpaCache.otherPlayer = null
        tpaCache.wait = false

        if (p.hasPermission("totalessentials.bypass.teleport")) {
            DataTeleport.remove(p)
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

        TotalEssentials.basePlugin.getTask().async {
            waitFor(time.toLong() * 20)
            try {
                switchContext(SynchronizationContext.SYNC)
                DataTeleport.remove(p)
                p.teleport(s.location)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }

        return false
    }
}
