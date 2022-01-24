package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVanish : CommandCreator {
    override val active: Boolean = MainConfig.vanishActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "vanish"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.vanish"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage =
        listOf(
            "P_/vanish",
            "essentialsk.commands.vanish.other_/vanish <PlayerName>"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.vanish.other")) {
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player is online
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            if (PlayerDataDAO[p]?.switchVanish() ?: return false) {
                p.sendMessage(GeneralLang.vanishSendOtherActive)
                s.sendMessage(GeneralLang.vanishSendActivatedOther.replace("%player%", p.name))
            } else {
                p.sendMessage(GeneralLang.vanishSendOtherDisable)
                s.sendMessage(GeneralLang.vanishSendDisabledOther.replace("%player%", p.name))
            }

            return false
        }

        if (PlayerDataDAO[s as Player]?.switchVanish() ?: return false) {
            s.sendMessage(GeneralLang.vanishSendActive)
        } else {
            s.sendMessage(GeneralLang.vanishSendDisable)
        }
        return false
    }
}
