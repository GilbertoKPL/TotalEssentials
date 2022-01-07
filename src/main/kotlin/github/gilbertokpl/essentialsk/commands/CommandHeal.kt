package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "heal"
    override val timeCoolDown: Long? = null
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
            if (s is Player && !s.hasPermission("essentialsk.commands.heal.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (MainConfig.getInstance().healNeedHealBelow && ReflectUtil.getInstance().getHealth(p) >= 20.0) {
                s.sendMessage(GeneralLang.getInstance().healSendOtherFullMessage)
                return false
            }

            ReflectUtil.getInstance().setHealth(p, 20)
            p.sendMessage(GeneralLang.getInstance().healSendOtherMessage)
            s.sendMessage(GeneralLang.getInstance().healSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        if (MainConfig.getInstance().healNeedHealBelow && ReflectUtil.getInstance().getHealth(s as Player) >= 20.0) {
            s.sendMessage(GeneralLang.getInstance().healSendFullMessage)
            return false
        }

        ReflectUtil.getInstance().setHealth(s as Player, 20)
        s.sendMessage(GeneralLang.getInstance().healSendMessage)
        return false
    }
}
