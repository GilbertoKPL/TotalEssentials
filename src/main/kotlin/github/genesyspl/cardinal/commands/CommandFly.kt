package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFly : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "fly"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.fly"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/fly",
        "cardinal.commands.fly.other_/fly <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.fly.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player is online
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (DataManager.getInstance().playerCacheV2[p.name.lowercase()]?.switchFly() ?: return false) {
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendOtherActive)
                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendActivatedOther.replace(
                        "%player",
                        p.name
                    )
                )
            } else {
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendOtherDisable)
                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendDisabledOther.replace(
                        "%player",
                        p.name
                    )
                )
            }

            return false
        }

        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().flyDisabledWorlds.contains((s as Player).location.world!!.name.lowercase())) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendDisabledWorld)
            return false
        }

        if (DataManager.getInstance().playerCacheV2[s.name.lowercase()]?.switchFly() ?: return false) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendActive)
        } else {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().flySendDisable)
        }
        return false
    }
}