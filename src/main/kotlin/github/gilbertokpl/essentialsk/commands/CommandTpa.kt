package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.TpaData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpa : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.tpaActivated,
            consoleCanUse = false,
            commandName = "tpa",
            timeCoolDown = null,
            permission = "essentialsk.commands.tpa",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf(
                "/tpa <playerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if player is same
        if (args[0].lowercase() == s.name.lowercase()) {
            s.sendMessage(LangConfig.tpaSameName)
            return false
        }

        //check if player is online
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        //check if player already send
        if (TpaData.checkTpa(s as Player)) {
            s.sendMessage(LangConfig.tpaAlreadySend)
            return false
        }

        //check if player has telepot request
        if (TpaData.checkOtherTpa(p)) {
            s.sendMessage(LangConfig.tpaAlreadyInAccept)
            return false
        }
        val time = MainConfig.tpaTimeToAccept

        TpaData.createNewTpa(s, p, time)

        s.sendMessage(LangConfig.tpaSendSuccess.replace("%player%", p.name))
        p.sendMessage(
            LangConfig.tpaOtherReceived.replace("%player%", s.name).replace("%time%", time.toString())
        )
        return false
    }
}
