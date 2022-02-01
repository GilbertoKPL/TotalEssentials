package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFeed : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.feedActivated,
            consoleCanUse = true,
            commandName = "feed",
            timeCoolDown = null,
            permission = "essentialsk.commands.feed",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "P_/feed",
                "essentialsk.commands.feed.other_/feed <playerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.feed.other")) {
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            if (p.foodLevel >= MAX_PLAYER_FOOD && MainConfig.feedNeedEatBelow) {
                s.sendMessage(GeneralLang.feedSendOtherFullMessage)
                return false
            }

            p.foodLevel = MAX_PLAYER_FOOD
            p.sendMessage(GeneralLang.feedSendOtherMessage)
            s.sendMessage(GeneralLang.feedSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        if ((s as Player).foodLevel >= MAX_PLAYER_FOOD && MainConfig.feedNeedEatBelow) {
            s.sendMessage(GeneralLang.feedSendFullMessage)
            return false
        }
        s.foodLevel = MAX_PLAYER_FOOD
        s.sendMessage(GeneralLang.feedSendMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_FOOD = 20
    }
}
