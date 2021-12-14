package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.heal"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/heal", "essentialsk.commands.heal.other_/heal <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && s.hasPermission("essentialsk.commands.heal.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (p.health >= 20.0 && MainConfig.getInstance().healNeedHealBelow) {
                s.sendMessage(GeneralLang.getInstance().healSendOtherFullMessage)
                return false
            }

            p.health = 20.0
            p.sendMessage(GeneralLang.getInstance().healSendOtherMessage)
            s.sendMessage(GeneralLang.getInstance().healSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        if ((s as Player).health >= 20.0 && MainConfig.getInstance().healNeedHealBelow) {
            s.sendMessage(GeneralLang.getInstance().healSendFullMessage)
            return false
        }

        s.health = 20.0
        s.sendMessage(GeneralLang.getInstance().healSendMessage)
        return true
    }
}