package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.WarpData
import github.gilbertokpl.total.util.MainUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : github.gilbertokpl.base.external.command.CommandCreator("setwarp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.warpsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "essentialsk.commands.setwarp",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf(
                "/setwarp <warpName>",
                "/setwarp <warpName> <worldName> <x> <y> <z>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        //check if warp name do not contain special
        if (MainUtil.checkSpecialCaracteres(args[0])) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        //check if exist
        if (WarpData.checkIfWarpExist(args[0])) {
            s.sendMessage(LangConfig.warpsNameAlreadyExist)
            return false
        }

        //check if a command have a location
        if (args.size == 5) {
            //check location
            val loc = try {
                Location(
                    github.gilbertokpl.total.TotalEssentials.instance.server.getWorld(args[1]),
                    args[2].toDouble(),
                    args[3].toDouble(),
                    args[4].toDouble()
                )
            } catch (e: Throwable) {
                return true
            }

            WarpData.warpLocation[args[0]] = loc

            s.sendMessage(
                LangConfig.warpsCreated.replace(
                    "%warp%",
                    args[0].lowercase()
                )
            )

            return false
        }

        if (args.size == 1 && s is Player) {
            WarpData.warpLocation[args[0]] = s.location

            s.sendMessage(
                LangConfig.warpsCreated.replace(
                    "%warp%",
                    args[0].lowercase()
                )
            )
            return false
        }

        //return true to send command usage
        return true
    }
}
