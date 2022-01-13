package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFly : CommandCreator {
    override val active: Boolean = MainConfig.flyActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "fly"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.fly"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/fly",
        "essentialsk.commands.fly.other_/fly <PlayerName>"
    )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.fly.other")) {
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player is online
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            if (PlayerDataV2[p]?.switchFly() ?: return false) {
                p.sendMessage(GeneralLang.flySendOtherActive)
                s.sendMessage(GeneralLang.flySendActivatedOther.replace("%player", p.name))
            } else {
                p.sendMessage(GeneralLang.flySendOtherDisable)
                s.sendMessage(GeneralLang.flySendDisabledOther.replace("%player", p.name))
            }

            return false
        }

        if (MainConfig.flyDisabledWorlds.contains((s as Player).location.world!!.name.lowercase())) {
            s.sendMessage(GeneralLang.flySendDisabledWorld)
            return false
        }

        if (PlayerDataV2[s]?.switchFly() ?: return false) {
            s.sendMessage(GeneralLang.flySendActive)
        } else {
            s.sendMessage(GeneralLang.flySendDisable)
        }
        return false
    }
}
