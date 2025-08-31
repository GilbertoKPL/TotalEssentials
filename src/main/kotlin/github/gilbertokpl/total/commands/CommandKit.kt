package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKit : CommandCreator("kit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("kits"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.kit",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf("/kit", "/kit <kitName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // if sender is not player or menu kits disabled, show kit list
        if (s !is Player || (args.isEmpty() && !MainConfig.kitsMenuKit)) {
            s.sendMessage(
                LangConfig.kitsList.replace(
                    "%kits%",
                    KitsData.kitTime.getMap().map { it.key }.toString()
                )
            )
            return false
        }

        // open GUI if no argument
        if (args.isEmpty()) {
            Data.kitInventoryCache[1].also {
                it ?: run {
                    s.sendMessage(LangConfig.kitsNotExistKits)
                    return false
                }
                s.openInventory(it)
            }
            return false
        }

        // check if kit exists
        val kitName = args[0].lowercase()
        if (!KitsData.checkIfExist(kitName)) {
            s.sendMessage(
                LangConfig.kitsList.replace(
                    "%kits%",
                    KitsData.kitTime.getMap().map { it.key }.toString()
                )
            )
            return false
        }

        // give kit
        ItemUtil.pickupKit(s, kitName)
        return false
    }
}
