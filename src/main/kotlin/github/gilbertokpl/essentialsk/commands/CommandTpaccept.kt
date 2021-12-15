package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.tpa"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/tpaccept"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = Dao.getInstance().tpaHash[s as Player] ?: run {
            s.sendMessage(GeneralLang.getInstance().tpaNotAnyRequest)
            return false
        }

        //remove checker
        val value = Dao.getInstance().tpAcceptHash[p]

        Dao.getInstance().tpaHash.remove(s)

        if (value!! == 1) {
            Dao.getInstance().tpAcceptHash.remove(p)
        }

        //check if player is online
        if (EssentialsK.instance.server.getPlayer(p.name) == null) {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        s.sendMessage(GeneralLang.getInstance().tpaRequestAccepted.replace("%player%", p.name))

        if (p.hasPermission("essentialsk.bypass.teleport")) {
            Dao.getInstance().tpaHash.remove(p)
            p.sendMessage(GeneralLang.getInstance().tpaRequestOtherNoDelayAccepted.replace("%player%", s.name))
            p.teleport(s.location)
            return false
        }

        val time = MainConfig.getInstance().tpaTimeToTeleport

        p.sendMessage(
            GeneralLang.getInstance().tpaRequestOtherAccepted.replace("%player%", s.name)
                .replace("%time%", time.toString())
        )

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        exe {
            p.teleport(s.location)
            Dao.getInstance().tpaHash.remove(p)
        }

        return false
    }
}