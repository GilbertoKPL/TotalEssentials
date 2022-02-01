package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.WarpData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelWarp : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.warpsActivated,
            consoleCanUse = true,
            commandName = "delwarp",
            timeCoolDown = null,
            permission = "essentialsk.commands.delwarp",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf("/delwarp <warpName>")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.warpsNameLength)
            return false
        }

        WarpData[args[0].lowercase()] ?: run {
            s.sendMessage(GeneralLang.warpsNameDontExist)
            return false
        }

        WarpData.del(args[0].lowercase())

        s.sendMessage(GeneralLang.warpsWarpRemoved.replace("%warp%", args[0].lowercase()))

        return false
    }
}
