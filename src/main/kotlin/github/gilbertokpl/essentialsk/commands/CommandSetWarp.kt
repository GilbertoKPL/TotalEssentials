package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.WarpDataV2
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.MainUtil
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : CommandCreator {
    override val active: Boolean = MainConfig.warpsActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "setwarp"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.setwarp"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf(
            "/setwarp <warpName>",
            "/setwarp <warpName> <worldName> <x> <y> <z>"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.warpsNameLength)
            return false
        }

        //check if warp name do not contain special
        if (MainUtil.checkSpecialCaracteres(args[0])) {
            s.sendMessage(GeneralLang.generalSpecialCaracteresDisabled)
            return false
        }

        //check if exist
        if (WarpDataV2[args[0]] != null) {
            s.sendMessage(GeneralLang.warpsNameAlreadyExist)
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

            s.sendMessage(GeneralLang.generalSendingInfoToDb)
            WarpDataV2.set(args[0], loc, s)


            return false
        }

        if (args.size == 1 && s is Player) {
            s.sendMessage(GeneralLang.generalSendingInfoToDb)
            WarpDataV2.set(args[0], s.location, s)
            return false
        }

        //return true to send command usage
        return true
    }
}
