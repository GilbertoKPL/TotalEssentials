package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHat : github.gilbertokpl.core.external.command.CommandCreator("hat") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("h", "chapeu"),
            active = MainConfig.hatActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.hat",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/hat"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val p = s as Player

        val itemHand = try {
            p.inventory.itemInMainHand
        } catch (e: NoSuchMethodError) {
            p.itemInHand
        }

        if (itemHand.type == Material.AIR) {
            p.sendMessage(LangConfig.hatNotFound)
            return false
        }

        val helmet = p.inventory.helmet

        p.inventory.helmet = itemHand

        try {
            p.inventory.setItemInMainHand(helmet)
        } catch (e: NoSuchMethodError) {
            p.setItemInHand(helmet)
        }


        p.sendMessage(LangConfig.hatSuccess)

        return false
    }
}