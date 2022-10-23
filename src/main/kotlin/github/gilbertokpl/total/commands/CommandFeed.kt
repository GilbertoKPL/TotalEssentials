package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFeed : github.gilbertokpl.base.external.command.CommandCreator("feed") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("comer"),
            active = MainConfig.feedActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.feed",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/feed",
                "totalessentials.commands.feed.other_/feed <playerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("totalessentials.commands.feed.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (p.foodLevel >= MAX_PLAYER_FOOD && MainConfig.feedNeedEatBelow) {
                s.sendMessage(LangConfig.feedOtherFullMessage)
                return false
            }

            p.foodLevel = MAX_PLAYER_FOOD
            p.sendMessage(LangConfig.feedOtherMessage)
            s.sendMessage(
                LangConfig.feedSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                )
            )

            return false
        }

        if ((s as Player).foodLevel >= MAX_PLAYER_FOOD && MainConfig.feedNeedEatBelow) {
            s.sendMessage(LangConfig.feedFullMessage)
            return false
        }
        s.foodLevel = MAX_PLAYER_FOOD
        s.sendMessage(LangConfig.feedMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_FOOD = 20
    }
}
