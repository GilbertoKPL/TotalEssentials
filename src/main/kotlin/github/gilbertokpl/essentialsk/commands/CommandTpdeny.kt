package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.TpaData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpdeny : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.tpaActivated,
            consoleCanUse = false,
            commandName = "tpdeny",
            timeCoolDown = null,
            permission = "essentialsk.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/tpdeny")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = TpaData.getTpa(s as Player) ?: run {
            s.sendMessage(GeneralLang.tpaNotAnyRequestToDeny)
            return false
        }

        TpaData.remove(p)

        s.sendMessage(GeneralLang.tpaRequestDeny.replace("%player%", p.name))

        if (EssentialsK.instance.server.getPlayer(p.name) != null) {
            p.sendMessage(GeneralLang.tpaRequestOtherDeny.replace("%player%", s.name))
        }
        return false
    }
}
