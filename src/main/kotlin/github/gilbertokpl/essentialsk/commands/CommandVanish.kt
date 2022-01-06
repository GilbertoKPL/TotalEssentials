package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVanish : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "vanish"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.vanish"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/vanish",
        "essentialsk.commands.vanish.other_/vanish <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.vanish.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player is online
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!.switchVanish()) {
                p.sendMessage(GeneralLang.getInstance().vanishSendOtherActive)
                s.sendMessage(GeneralLang.getInstance().vanishSendActivatedOther.replace("%player%", p.name))
            } else {
                p.sendMessage(GeneralLang.getInstance().vanishSendOtherDisable)
                s.sendMessage(GeneralLang.getInstance().vanishSendDisabledOther.replace("%player%", p.name))
            }

            return false
        }

        if (DataManager.getInstance().playerCacheV2[s.name.lowercase()]!!.switchVanish()) {
            s.sendMessage(GeneralLang.getInstance().vanishSendActive)
        } else {
            s.sendMessage(GeneralLang.getInstance().vanishSendDisable)
        }
        return false
    }
}