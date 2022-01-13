package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : CommandCreator {
    override val active: Boolean = MainConfig.healActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "heal"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.heal"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/heal",
        "essentialsk.commands.heal.other_/heal <playerName>"
    )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.heal.other")) {
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            if (MainConfig.healNeedHealBelow && ReflectUtil.getHealth(p) >= MAX_PLAYER_HEAL) {
                s.sendMessage(GeneralLang.healSendOtherFullMessage)
                return false
            }

            ReflectUtil.setHealth(p, 20)
            p.sendMessage(GeneralLang.healSendOtherMessage)
            s.sendMessage(GeneralLang.healSendSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        if (MainConfig.healNeedHealBelow && ReflectUtil.getHealth(s as Player) >= MAX_PLAYER_HEAL) {
            s.sendMessage(GeneralLang.healSendFullMessage)
            return false
        }

        ReflectUtil.setHealth(s as Player, MAX_PLAYER_HEAL)
        s.sendMessage(GeneralLang.healSendMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_HEAL = 20
    }
}
