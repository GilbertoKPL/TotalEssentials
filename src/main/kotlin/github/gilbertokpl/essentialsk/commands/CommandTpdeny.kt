package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.TpaData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpdeny : CommandCreator {
    override val active: Boolean = MainConfig.tpaActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "tpdeny"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.tpa"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/tpdeny")

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
