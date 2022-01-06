package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpdeny : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "tpdeny"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.tpa"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/tpdeny"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = DataManager.getInstance().tpaHash[s as Player] ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaNotAnyRequestToDeny)
            return false
        }

        //remove checker

        val value = DataManager.getInstance().tpAcceptHash[p] ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaNotAnyRequestToDeny)
            return false
        }

        DataManager.getInstance().tpaHash.remove(s)
        DataManager.getInstance().tpaHash.remove(p)

        if (value == 1) {
            DataManager.getInstance().tpAcceptHash.remove(p)
        }

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaRequestDeny.replace(
                "%player%",
                p.name
            )
        )

        if (github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(p.name) != null) {
            p.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaRequestOtherDeny.replace(
                    "%player%",
                    s.name
                )
            )
        }
        return false
    }
}