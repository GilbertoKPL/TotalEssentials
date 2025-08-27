package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
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
            usage = listOf("/hat")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val p = s as Player

        // Get item in main hand
        val itemHand = try {
            p.inventory.itemInMainHand
        } catch (e: NoSuchMethodError) {
            p.itemInHand
        }

        // Check if the hand is empty
        if (itemHand.type == Material.AIR) {
            p.sendMessage(LangConfig.hatNotFound)
            return false
        }

        // Swap helmet with the item in hand
        val helmet = p.inventory.helmet
        p.inventory.helmet = itemHand
        PlayerUtil.setItemInMainHand(p, helmet)

        // Notify player
        p.sendMessage(LangConfig.hatSuccess)
        return false
    }
}
