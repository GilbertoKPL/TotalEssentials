package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.FancyChat
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKit : CommandManager("kit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("kits"),
            active = MainConfig.kitsActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.kit",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf("/kit", "/kit <kitName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // if sender is not player or menu kits disabled, show kit list
        if (sender !is Player || (args.isEmpty() && !MainConfig.kitsMenuKit)) {
            val kits = KitsData.kitTime.getMap().keys.toList()
            val sentFancyMessage = sender is Player && FancyChat.sendCommandList(
                sender,
                LangConfig.kitsList,
                "%kits%",
                kits.map { kit ->
                    FancyChat.Action("§e$kit", "/kit $kit")
                }
            )
            if (!sentFancyMessage) {
                sender.sendMessage(
                    LangConfig.kitsList.replace(
                        "%kits%",
                        kits.toString()
                    )
                )
            }
            return false
        }

        // open GUI if no argument
        if (args.isEmpty()) {
            Data.kitInventoryCache[1].also {
                it ?: run {
                    sender.sendMessage(LangConfig.kitsNotExistKits)
                    return false
                }
                sender.openInventory(it)
            }
            return false
        }

        // check if kit exists
        val kitName = args[0].lowercase()
        if (!KitsData.checkIfExist(kitName)) {
            val kits = KitsData.kitTime.getMap().keys.toList()
            val sentFancyMessage = FancyChat.sendCommandList(
                sender,
                LangConfig.kitsList,
                "%kits%",
                kits.map { kit ->
                    FancyChat.Action("§e$kit", "/kit $kit")
                }
            )
            if (!sentFancyMessage) {
                sender.sendMessage(
                    LangConfig.kitsList.replace(
                        "%kits%",
                        kits.toString()
                    )
                )
            }
            return false
        }

        // give kit
        ItemUtil.pickupKit(sender, kitName)
        return false
    }
}
