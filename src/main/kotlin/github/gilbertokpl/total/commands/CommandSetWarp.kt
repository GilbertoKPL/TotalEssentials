package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : CommandManager("setwarp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.warpsActivated,
            target = CommandTargetType.ALL,
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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // check warp name length
        if (args[0].length > 16) {
            sender.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        // check for special characters
        if (ServerUtil.hasSpecialCharacters(args[0])) {
            sender.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        // check if warp already exists
        if (WarpData.checkIfWarpExist(args[0])) {
            sender.sendMessage(LangConfig.warpsNameAlreadyExist)
            return false
        }

        // create warp from command location
        if (args.size == 5) {
            val loc = try {
                Location(
                    TotalEssentials.getInstance().server.getWorld(args[1]),
                    args[2].toDouble(),
                    args[3].toDouble(),
                    args[4].toDouble()
                )
            } catch (_: Throwable) {
                return true
            }

            WarpData.warpLocation[args[0]] = loc
            sender.sendMessage(LangConfig.warpsCreated.replace("%warp%", args[0].lowercase()))
            return false
        }

        // create warp at player location
        if (args.size == 1 && sender is Player) {
            WarpData.warpLocation[args[0]] = sender.location
            sender.sendMessage(LangConfig.warpsCreated.replace("%warp%", args[0].lowercase()))
            return false
        }

        return true
    }
}
