package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEchest : github.gilbertokpl.base.external.command.CommandCreator("echest") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("ec"),
            active = MainConfig.echestActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "essentialsk.commands.ec",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/echest",
                "essentialsk.commands.ec.other_/ec <PlayerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            s.sendMessage(LangConfig.echestSuccess)
            val inv = (s as Player).enderChest
            s.openInventory(inv)
            return false
        }

        //admin
        if (s.hasPermission("essentialsk.commands.ec.other")) {
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            s.sendMessage(
                LangConfig.echestOtherSuccess.replace(
                    "%player%",
                    p.name
                )
            )
            val inv = p.enderChest
            (s as Player).openInventory(inv)
            return false
        }
        return true
    }
}
