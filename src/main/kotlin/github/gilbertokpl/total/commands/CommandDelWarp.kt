package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.cache.data.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender

class CommandDelWarp : CommandCreator("delwarp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("deletarwarp"),
            active = MainConfig.warpsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.delwarp",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf("/delwarp <warpName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val warpName = args[0].lowercase()

        // --------------------------------------------------------
        // Check length of warp name
        // --------------------------------------------------------
        if (warpName.length > 16) {
            s.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        // --------------------------------------------------------
        // Check if warp exists
        // --------------------------------------------------------
        if (!WarpData.checkIfWarpExist(warpName)) {
            s.sendMessage(LangConfig.warpsNameDontExist)
            return false
        }

        // --------------------------------------------------------
        // Delete warp
        // --------------------------------------------------------
        WarpData.deleteWarp(warpName)

        // --------------------------------------------------------
        // Notify player
        // --------------------------------------------------------
        s.sendMessage(LangConfig.warpsRemoved.replace("%warp%", warpName))

        return false
    }
}
