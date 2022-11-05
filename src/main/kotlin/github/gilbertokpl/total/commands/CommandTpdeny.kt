package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.internal.TpaData
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpdeny : github.gilbertokpl.core.external.command.CommandCreator("tpdeny") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.tpaActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/tpdeny")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val p = TpaData.getTpa(s as Player) ?: run {
            s.sendMessage(LangConfig.tpaNotAnyRequestToDeny)
            return false
        }

        TpaData.remove(p)

        s.sendMessage(LangConfig.tpaRequestDeny.replace("%player%", p.name))

        if (github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(p.name) != null) {
            p.sendMessage(
                LangConfig.tpaRequestOtherDeny.replace(
                    "%player%",
                    s.name
                )
            )
        }
        return false
    }
}
