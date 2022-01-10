package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : CommandCreator {
    override val consoleCanUse: Boolean = false
    override val commandName = "tpaccept"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.tpa"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/tpaccept"
    )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = DataManager.tpaHash[s as Player] ?: run {
            s.sendMessage(GeneralLang.tpaNotAnyRequest)
            return false
        }

        //remove checker
        val value = DataManager.tpAcceptHash[p] ?: run {
            s.sendMessage(GeneralLang.tpaNotAnyRequest)
            return false
        }

        DataManager.tpaHash.remove(s)

        if (value == 1) {
            DataManager.tpAcceptHash.remove(p)
        }

        //check if player is online
        if (EssentialsK.instance.server.getPlayer(p.name) == null) {
            s.sendMessage(GeneralLang.generalPlayerNotOnline)
            return false
        }

        s.sendMessage(GeneralLang.tpaRequestAccepted.replace("%player%", p.name))

        if (p.hasPermission("essentialsk.bypass.teleport")) {
            DataManager.tpaHash.remove(p)
            p.sendMessage(GeneralLang.tpaRequestOtherNoDelayAccepted.replace("%player%", s.name))
            p.teleport(s.location)
            return false
        }

        val time = MainConfig.tpaTimeToTeleport

        p.sendMessage(
            GeneralLang.tpaRequestOtherAccepted.replace("%player%", s.name)
                .replace("%time%", time.toString())
        )

        val exe = TaskUtil.teleportExecutor(time)

        exe {
            p.teleport(s.location)
            DataManager.tpaHash.remove(p)
        }

        return false
    }
}
