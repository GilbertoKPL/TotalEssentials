package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.LightCache.switchLight
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLight : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.lightActivated,
            consoleCanUse = true,
            commandName = "light",
            timeCoolDown = null,
            permission = "essentialsk.commands.light",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "P_/light",
                "essentialsk.commands.light.other_/light <playerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.light.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (PlayerData[p]?.switchLight(p) ?: return false) {
                p.sendMessage(LangConfig.lightOtherActive)
                s.sendMessage(
                    LangConfig.lightActivatedOther
                        .replace("%player%", p.name.lowercase())
                )
            } else {
                p.sendMessage(LangConfig.lightOtherDisable)
                s.sendMessage(
                    LangConfig.lightDisabledOther
                        .replace("%player%", p.name.lowercase())
                )
            }

            return false
        }

        if (PlayerData[s as Player]?.switchLight(s) ?: return false) {
            s.sendMessage(LangConfig.lightActive)
        } else {
            s.sendMessage(LangConfig.lightDisable)
        }

        return false
    }
}
