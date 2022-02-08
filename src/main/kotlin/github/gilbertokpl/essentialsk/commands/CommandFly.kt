package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.FlyCache.switchFly
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFly : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.flyActivated,
            consoleCanUse = true,
            commandName = "fly",
            timeCoolDown = null,
            permission = "essentialsk.commands.fly",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "P_/fly",
                "essentialsk.commands.fly.other_/fly <PlayerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        //admin
        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.fly.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player is online
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (PlayerData[p]?.switchFly(p) ?: return false) {
                p.sendMessage(LangConfig.flyOtherActive)
                s.sendMessage(LangConfig.flyActivatedOther.replace("%player", p.name))
            } else {
                p.sendMessage(LangConfig.flyOtherDisable)
                s.sendMessage(LangConfig.flyDisabledOther.replace("%player", p.name))
            }

            return false
        }

        if (MainConfig.flyDisabledWorlds.contains((s as Player).location.world!!.name.lowercase())) {
            s.sendMessage(LangConfig.flyDisabledWorld)
            return false
        }

        if (PlayerData[s]?.switchFly(s) ?: return false) {
            s.sendMessage(LangConfig.flyActive)
        } else {
            s.sendMessage(LangConfig.flyDisable)
        }
        return false
    }
}
