package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "tpaccept"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.tpa"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/tpaccept"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = DataManager.getInstance().tpaHash[s as Player] ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaNotAnyRequest)
            return false
        }

        //remove checker
        val value = DataManager.getInstance().tpAcceptHash[p] ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaNotAnyRequest)
            return false
        }

        DataManager.getInstance().tpaHash.remove(s)

        if (value == 1) {
            DataManager.getInstance().tpAcceptHash.remove(p)
        }

        //check if player is online
        if (github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(p.name) == null) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaRequestAccepted.replace(
                "%player%",
                p.name
            )
        )

        if (p.hasPermission("cardinal.bypass.teleport")) {
            DataManager.getInstance().tpaHash.remove(p)
            p.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaRequestOtherNoDelayAccepted.replace(
                    "%player%",
                    s.name
                )
            )
            p.teleport(s.location)
            return false
        }

        val time = github.genesyspl.cardinal.configs.MainConfig.getInstance().tpaTimeToTeleport

        p.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpaRequestOtherAccepted.replace(
                "%player%",
                s.name
            )
                .replace("%time%", time.toString())
        )

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        exe {
            p.teleport(s.location)
            DataManager.getInstance().tpaHash.remove(p)
        }

        return false
    }
}