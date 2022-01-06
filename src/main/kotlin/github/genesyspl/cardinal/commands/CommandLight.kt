package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLight : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "light"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.light"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/light", "cardinal.commands.light.other_/light <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.light.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (DataManager.getInstance().playerCacheV2[p.name.lowercase()]?.switchLight() ?: return false) {
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().lightSendOtherActive)
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().lightSendActivatedOther)
            } else {
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().lightSendOtherDisable)
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().lightSendDisabledOther)
            }

            return false
        }

        if (DataManager.getInstance().playerCacheV2[s.name.lowercase()]?.switchLight() ?: return false) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().lightSendActive)
        } else {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().lightSendDisable)
        }

        return false
    }
}