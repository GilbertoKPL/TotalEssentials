package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : CommandCreator("heal") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("h", "vida"),
            active = MainConfig.healActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.heal",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/heal",
                "totalessentials.commands.heal.other_/heal <playerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) return true

        if (args.size == 1) {
            // Check permission for other player
            if (s is Player && !s.hasPermission("totalessentials.commands.heal.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            // Get target player
            val p = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            // Check if target needs healing
            if (MainConfig.healNeedHealBelow &&
                TotalEssentials.getCore().getReflection().getHealth(p) >= MAX_PLAYER_HEAL
            ) {
                s.sendMessage(LangConfig.healOtherFullMessage)
                return false
            }

            // Heal target
            TotalEssentials.getCore().getReflection().setHealth(p, MAX_PLAYER_HEAL)
            p.sendMessage(LangConfig.healOtherMessage)
            s.sendMessage(LangConfig.healSuccessOtherMessage.replace("%player%", p.name))
            return false
        }

        // Check if sender needs healing
        if (MainConfig.healNeedHealBelow &&
            TotalEssentials.getCore().getReflection().getHealth(s as Player) >= MAX_PLAYER_HEAL
        ) {
            s.sendMessage(LangConfig.healFullMessage)
            return false
        }

        // Heal sender
        TotalEssentials.getCore().getReflection().setHealth(s as Player, MAX_PLAYER_HEAL)
        s.sendMessage(LangConfig.healMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_HEAL = 20 // Maximum health
    }
}
