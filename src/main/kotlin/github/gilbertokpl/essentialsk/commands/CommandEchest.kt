package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEchest : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.echestActivated,
            consoleCanUse = false,
            commandName = "echest",
            timeCoolDown = null,
            permission = "essentialsk.commands.ec",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "/echest",
                "essentialsk.commands.ec.other_/ec <PlayerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            s.sendMessage(LangConfig.echestSendSuccess)
            val inv = (s as Player).enderChest
            s.openInventory(inv)
            return false
        }

        //admin
        if (s.hasPermission("essentialsk.commands.ec.other")) {
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            s.sendMessage(LangConfig.echestSendOtherSuccess.replace("%player%", p.name))
            val inv = p.enderChest
            (s as Player).openInventory(inv)
            return false
        }
        return true
    }
}
