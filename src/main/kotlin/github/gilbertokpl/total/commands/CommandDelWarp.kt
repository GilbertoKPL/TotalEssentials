package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender

class CommandDelWarp : CommandManager("delwarp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("deletarwarp"),
            active = MainConfig.warpsActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.delwarp",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/delwarp <warpName>")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val warpName = args[0].lowercase()

        // --------------------------------------------------------
        // Check length of warp name
        // --------------------------------------------------------
        if (warpName.length > 16) {
            sender.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        // --------------------------------------------------------
        // Check if warp exists
        // --------------------------------------------------------
        if (!WarpData.checkIfWarpExist(warpName)) {
            sender.sendMessage(LangConfig.warpsNameDontExist)
            return false
        }

        // --------------------------------------------------------
        // Delete warp
        // --------------------------------------------------------
        WarpData.deleteWarp(warpName)

        // --------------------------------------------------------
        // Notify player
        // --------------------------------------------------------
        sender.sendMessage(LangConfig.warpsRemoved.replace("%warp%", warpName))

        return false
    }
}
