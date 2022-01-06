package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVanish : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "vanish"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.vanish"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/vanish",
        "cardinal.commands.vanish.other_/vanish <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.vanish.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player is online
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!.switchVanish()) {
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().vanishSendOtherActive)
                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().vanishSendActivatedOther.replace(
                        "%player%",
                        p.name
                    )
                )
            } else {
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().vanishSendOtherDisable)
                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().vanishSendDisabledOther.replace(
                        "%player%",
                        p.name
                    )
                )
            }

            return false
        }

        if (DataManager.getInstance().playerCacheV2[s.name.lowercase()]!!.switchVanish()) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().vanishSendActive)
        } else {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().vanishSendDisable)
        }
        return false
    }
}