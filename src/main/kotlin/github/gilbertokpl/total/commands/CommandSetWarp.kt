package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MainUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : github.gilbertokpl.core.external.command.CommandCreator("setwarp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.warpsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.setwarp",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf(
                "/setwarp <warpName>",
                "/setwarp <warpName> <worldName> <x> <y> <z>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // check warp name length
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        // check for special characters
        if (MainUtil.checkSpecialCharacters(args[0])) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        // check if warp already exists
        if (WarpData.checkIfWarpExist(args[0])) {
            s.sendMessage(LangConfig.warpsNameAlreadyExist)
            return false
        }

        // create warp from command location
        if (args.size == 5) {
            val loc = try {
                Location(
                    TotalEssentialsJava.getInstance().server.getWorld(args[1]),
                    args[2].toDouble(),
                    args[3].toDouble(),
                    args[4].toDouble()
                )
            } catch (e: Throwable) {
                return true
            }

            WarpData.warpLocation[args[0]] = loc
            s.sendMessage(LangConfig.warpsCreated.replace("%warp%", args[0].lowercase()))
            return false
        }

        // create warp at player location
        if (args.size == 1 && s is Player) {
            WarpData.warpLocation[args[0]] = s.location
            s.sendMessage(LangConfig.warpsCreated.replace("%warp%", args[0].lowercase()))
            return false
        }

        return true
    }
}
