package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class CommandTpa : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "tpa"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.tpa"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/tpa <playerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if player is same
        if (args[0].lowercase() == s.name.lowercase()) {
            s.sendMessage(GeneralLang.getInstance().tpaSameName)
            return false
        }

        //check if player is online
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        //check if player already send
        if (DataManager.getInstance().tpaHash.contains(s) || DataManager.getInstance().tpaHash.containsValue(p)) {
            s.sendMessage(GeneralLang.getInstance().tpaAlreadySend)
            return false
        }

        //check if player has telepot request
        if (DataManager.getInstance().tpaHash.contains(p)) {
            s.sendMessage(GeneralLang.getInstance().tpaAlreadyInAccept)
            return false
        }

        val time = MainConfig.getInstance().tpaTimeToAccept

        coolDown(s as Player, p, time)

        s.sendMessage(GeneralLang.getInstance().tpaSendSuccess.replace("%player%", p.name))
        p.sendMessage(
            GeneralLang.getInstance().tpaOtherReceived.replace("%player%", s.name).replace("%time%", time.toString())
        )
        return false
    }


    private fun coolDown(pSender: Player, pReceived: Player, time: Int) {
        DataManager.getInstance().tpaHash[pSender] = pReceived
        DataManager.getInstance().tpaHash[pReceived] = pSender
        DataManager.getInstance().tpAcceptHash[pSender] = 1

        CompletableFuture.runAsync({
            TimeUnit.SECONDS.sleep(time.toLong())
            try {
                val value = DataManager.getInstance().tpAcceptHash[pSender]
                if (value != null && value == 1) {
                    DataManager.getInstance().tpAcceptHash.remove(pSender)
                    EssentialsK.instance.server.scheduler.runTask(EssentialsK.instance, Runnable {
                        DataManager.getInstance().tpaHash.remove(pSender)
                        DataManager.getInstance().tpaHash.remove(pReceived)
                    })
                }
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
        }, TaskUtil.getInstance().getTeleportExecutor())
    }
}
