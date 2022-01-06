package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFeed : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "feed"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.feed"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/feed", "cardinal.commands.feed.other_/feed <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.feed.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (p.foodLevel >= 20 && github.genesyspl.cardinal.configs.MainConfig.getInstance().feedNeedEatBelow) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().feedSendOtherFullMessage)
                return false
            }

            p.foodLevel = 20
            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().feedSendOtherMessage)
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().feedSendSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                )
            )

            return false
        }

        if ((s as Player).foodLevel >= 15 && github.genesyspl.cardinal.configs.MainConfig.getInstance().feedNeedEatBelow) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().feedSendFullMessage)
            return false
        }
        s.foodLevel = 20
        s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().feedSendMessage)
        return false
    }
}