package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEchest : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "echest"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.ec"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/echest",
        "cardinal.commands.ec.other_/ec <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().echestSendSuccess)
            val inv = (s as Player).enderChest
            s.openInventory(inv)
            return false
        }

        val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().echestSendOtherSuccess.replace(
                "%player%",
                p.name
            )
        )
        val inv = p.enderChest
        (s as Player).openInventory(inv)

        return false
    }
}