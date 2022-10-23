package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : github.gilbertokpl.base.external.command.CommandCreator("heal") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("h", "vida"),
            active = MainConfig.healActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "essentialsk.commands.heal",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/heal",
                "essentialsk.commands.heal.other_/heal <playerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.heal.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (MainConfig.healNeedHealBelow && github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection()
                    .getHealth(p) >= MAX_PLAYER_HEAL
            ) {
                s.sendMessage(LangConfig.healOtherFullMessage)
                return false
            }

            github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().setHealth(p, 20)
            p.sendMessage(LangConfig.healOtherMessage)
            s.sendMessage(
                LangConfig.healSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                )
            )

            return false
        }

        if (MainConfig.healNeedHealBelow && github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection()
                .getHealth(s as Player) >= MAX_PLAYER_HEAL
        ) {
            s.sendMessage(LangConfig.healFullMessage)
            return false
        }

        github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().setHealth(s as Player, MAX_PLAYER_HEAL)
        s.sendMessage(LangConfig.healMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_HEAL = 20
    }
}
