package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.WarpData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.MainUtil
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.warpsActivated,
            consoleCanUse = true,
            commandName = "setwarp",
            timeCoolDown = null,
            permission = "essentialsk.commands.setwarp",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf(
                "/setwarp <warpName>",
                "/setwarp <warpName> <worldName> <x> <y> <z>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
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
        if (WarpData[args[0].lowercase()] != null) {
            s.sendMessage(LangConfig.warpsNameAlreadyExist)
            return false
        }

        //check if a command have a location
        if (args.size == 5) {
            //check location
            val loc = try {
                Location(
                    EssentialsK.instance.server.getWorld(args[1]),
                    args[2].toDouble(),
                    args[3].toDouble(),
                    args[4].toDouble()
                )
            } catch (e: Throwable) {
                return true
            }

            WarpData.set(args[0].lowercase(), loc)

            s.sendMessage(LangConfig.warpsCreated.replace("%warp%", args[0].lowercase()))

            return false
        }

        if (args.size == 1 && s is Player) {
            WarpData.set(args[0].lowercase(), s.location)

            s.sendMessage(LangConfig.warpsCreated.replace("%warp%", args[0].lowercase()))
            return false
        }

        //return true to send command usage
        return true
    }
}
