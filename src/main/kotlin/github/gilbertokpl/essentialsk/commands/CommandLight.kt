package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLight : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "light"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.light"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/light", "essentialsk.commands.light.other_/light <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.light.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!.switchLight()) {
                p.sendMessage(GeneralLang.getInstance().lightSendOtherActive)
                s.sendMessage(GeneralLang.getInstance().lightSendActivatedOther)
            } else {
                p.sendMessage(GeneralLang.getInstance().lightSendOtherDisable)
                s.sendMessage(GeneralLang.getInstance().lightSendDisabledOther)
            }

            return false
        }

        if (DataManager.getInstance().playerCacheV2[s.name.lowercase()]!!.switchLight()) {
            s.sendMessage(GeneralLang.getInstance().lightSendActive)
        } else {
            s.sendMessage(GeneralLang.getInstance().lightSendDisable)
        }

        return false
    }
}