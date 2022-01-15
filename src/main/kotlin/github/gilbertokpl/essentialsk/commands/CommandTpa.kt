package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.TpaData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class CommandTpa : CommandCreator {
    override val active: Boolean = MainConfig.tpaActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "tpa"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.tpa"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/tpa <playerName>"
    )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if player is same
        if (args[0].lowercase() == s.name.lowercase()) {
            s.sendMessage(GeneralLang.tpaSameName)
            return false
        }

        //check if player is online
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.generalPlayerNotOnline)
            return false
        }

        //check if player already send
        if (TpaData.checkTpa(s as Player)) {
            s.sendMessage(GeneralLang.tpaAlreadySend)
            return false
        }

        //check if player has telepot request
        if (TpaData.checkOtherTpa(p)) {
            s.sendMessage(GeneralLang.tpaAlreadyInAccept)
            return false
        }

        val time = MainConfig.tpaTimeToAccept

        TpaData.createNewTpa(s, p, time)

        s.sendMessage(GeneralLang.tpaSendSuccess.replace("%player%", p.name))
        p.sendMessage(
            GeneralLang.tpaOtherReceived.replace("%player%", s.name).replace("%time%", time.toString())
        )
        return false
    }
}
