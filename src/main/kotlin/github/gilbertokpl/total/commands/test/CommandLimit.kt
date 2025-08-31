package github.gilbertokpl.total.commands.test

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.test.LimitData
import github.gilbertokpl.total.cache.internal.Data.limitPlayerEdit
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import net.milkbowl.vault.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLimit : CommandCreator("limit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("limites", "limite"),
            active = MainConfig.limitActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.limit",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/limit",
                "P_/limit edit <group>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() && s is Player) {
            return false
        }
        if (args[0].contains("edit", true) && s is Player && s.hasPermission("totalessentials.commands.limit.edit")) {

            val perm = TotalEssentials.getPermission()

            if (perm is Permission) {
                if (!perm.groups.contains(args[1])) {
                    s.sendMessage(LangConfig.limitGroupDoNotExist)
                    return false
                }
            }
            if (perm is net.milkbowl.vault2.permission.Permission) {
                if (!perm.groups.contains(args[1])) {
                    s.sendMessage(LangConfig.limitGroupDoNotExist)
                    return false
                }
            }

            limitPlayerEdit[s] = args[1]

            val limit = LimitData.limitItems[args[1]]
            val inv = TotalEssentials.getInstance().server.createInventory(null, 54)
            if (limit != null) {
                for (i in limit) {
                    inv.addItem(i)
                }
            }

            s.openInventory(TotalEssentials.getInstance().server.createInventory(null, 54))
        }
        return true
    }
}