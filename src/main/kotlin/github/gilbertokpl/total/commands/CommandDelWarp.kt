package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.WarpData
import org.bukkit.command.CommandSender

class CommandDelWarp : github.gilbertokpl.core.external.command.CommandCreator("delwarp") {

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
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        if (!WarpData.checkIfWarpExist(args[0])) {
            s.sendMessage(LangConfig.warpsNameDontExist)
            return false
        }

        WarpData.delete(args[0].lowercase())

        s.sendMessage(
            LangConfig.warpsRemoved.replace(
                "%warp%",
                args[0].lowercase()
            )
        )

        return false
    }
}
