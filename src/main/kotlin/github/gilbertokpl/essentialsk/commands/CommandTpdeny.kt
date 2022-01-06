package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpdeny : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "tpdeny"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.tpa"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/tpdeny"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = DataManager.getInstance().tpaHash[s as Player] ?: run {
            s.sendMessage(GeneralLang.getInstance().tpaNotAnyRequestToDeny)
            return false
        }

        //remove checker

        val value = DataManager.getInstance().tpAcceptHash[p] ?: run {
            s.sendMessage(GeneralLang.getInstance().tpaNotAnyRequestToDeny)
            return false
        }

        DataManager.getInstance().tpaHash.remove(s)
        DataManager.getInstance().tpaHash.remove(p)

        if (value == 1) {
            DataManager.getInstance().tpAcceptHash.remove(p)
        }

        s.sendMessage(GeneralLang.getInstance().tpaRequestDeny.replace("%player%", p.name))

        if (EssentialsK.instance.server.getPlayer(p.name) != null) {
            p.sendMessage(GeneralLang.getInstance().tpaRequestOtherDeny.replace("%player%", s.name))
        }
        return false
    }
}