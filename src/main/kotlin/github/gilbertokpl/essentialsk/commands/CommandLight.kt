package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
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
                s.sendMessage(GeneralLang.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.generalPlayerNotOnline)
                return false
            }

            if (PlayerData[p]?.switchLight() ?: return false) {
                p.sendMessage(GeneralLang.lightSendOtherActive)
                s.sendMessage(
                    GeneralLang.lightSendActivatedOther
                        .replace("%player%", p.name.lowercase())
                )
            } else {
                p.sendMessage(GeneralLang.lightSendOtherDisable)
                s.sendMessage(
                    GeneralLang.lightSendDisabledOther
                        .replace("%player%", p.name.lowercase())
                )
            }

            return false
        }

        if (PlayerData[s as Player]?.switchLight() ?: return false) {
            s.sendMessage(GeneralLang.lightSendActive)
        } else {
            s.sendMessage(GeneralLang.lightSendDisable)
        }

        return false
    }
}
