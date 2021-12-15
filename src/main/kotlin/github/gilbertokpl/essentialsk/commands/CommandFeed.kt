package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFeed : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.feed"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/feed", "essentialsk.commands.feed.other_/feed <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.feed.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (p.foodLevel >= 20 && MainConfig.getInstance().feedNeedEatBelow) {
                s.sendMessage(GeneralLang.getInstance().feedSendOtherFullMessage)
                return false
            }

            p.foodLevel = 20
            p.sendMessage(GeneralLang.getInstance().feedSendOtherMessage)
            s.sendMessage(GeneralLang.getInstance().feedSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        if ((s as Player).foodLevel >= 15 && MainConfig.getInstance().feedNeedEatBelow) {
            s.sendMessage(GeneralLang.getInstance().feedSendFullMessage)
            return false
        }
        s.foodLevel = 20
        s.sendMessage(GeneralLang.getInstance().feedSendMessage)
        return false
    }
}