package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.WarpData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "setwarp"
    override val timeCoolDown : Long? = null
    override val permission: String = "essentialsk.commands.setwarp"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/setwarp <warpName>", "/setwarp <warpName> <worldName> <x> <y> <z>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.getInstance().warpsNameLength)
            return false
        }

        //check if warp name do not contain special
        if (PluginUtil.getInstance().checkSpecialCaracteres(args[0])) {
            s.sendMessage(GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        val warpName = args[0].lowercase()

        val warpInstance = WarpData(warpName)

        //check if exist
        if (!warpInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().warpsNameAlreadyExist)
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
            } catch (e: Exception) {
                return true
            }

            s.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
            warpInstance.setWarp(loc, s)


            return false
        }

        if (args.size == 1 && s is Player) {
            s.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
            warpInstance.setWarp(s.location, s)
            return false
        }

        //return true to send command usage
        return true
    }
}