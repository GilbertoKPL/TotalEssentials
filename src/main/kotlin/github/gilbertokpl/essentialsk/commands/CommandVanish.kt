package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.modify.VanishCache.switchVanish
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVanish : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.vanishActivated,
            consoleCanUse = true,
            commandName = "vanish",
            timeCoolDown = null,
            permission = "essentialsk.commands.vanish",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "P_/vanish",
                "essentialsk.commands.vanish.other_/vanish <PlayerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.vanish.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player is online
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (PlayerData[p]?.switchVanish(p) ?: return false) {
                p.sendMessage(LangConfig.vanishSendOtherActive)
                s.sendMessage(LangConfig.vanishSendActivatedOther.replace("%player%", p.name))
            } else {
                p.sendMessage(LangConfig.vanishSendOtherDisable)
                s.sendMessage(LangConfig.vanishSendDisabledOther.replace("%player%", p.name))
            }

            return false
        }

        if (PlayerData[s as Player]?.switchVanish(s) ?: return false) {
            s.sendMessage(LangConfig.vanishSendActive)
        } else {
            s.sendMessage(LangConfig.vanishSendDisable)
        }
        return false
    }
}
