package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.ReflectUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "heal"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.heal"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/heal", "cardinal.commands.heal.other_/heal <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.heal.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (github.genesyspl.cardinal.configs.MainConfig.getInstance().healNeedHealBelow && ReflectUtil.getInstance()
                    .getHealth(p) >= 20.0
            ) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().healSendOtherFullMessage)
                return false
            }

            ReflectUtil.getInstance().setHealth(p, 20)
            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().healSendOtherMessage)
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().healSendSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                )
            )

            return false
        }

        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().healNeedHealBelow && ReflectUtil.getInstance()
                .getHealth(s as Player) >= 20.0
        ) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().healSendFullMessage)
            return false
        }

        ReflectUtil.getInstance().setHealth(s as Player, 20)
        s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().healSendMessage)
        return false
    }
}