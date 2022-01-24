package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpeed : CommandCreator {
    override val active: Boolean = MainConfig.speedActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "speed"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.speed"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage =
        listOf(
            "/speed <value>",
            "/speed remove",
            "essentialsk.commands.speed.other_/speed <player> <value>",
            "essentialsk.commands.speed.other_/speed <player> remove"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if is 1
        if (args.size == 1 && s is Player) {

            val playerCache = PlayerDataDAO[s] ?: return false

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                playerCache.clearSpeed()
                s.sendMessage(GeneralLang.speedSendRemove)
                return false
            }

            //check int
            try {
                args[0].toInt()
            } catch (e: Throwable) {
                return true
            }

            //check if number is 0-10
            if (args[0].toInt() > 10 || args[0].toInt() < 0) {
                s.sendMessage(GeneralLang.speedSendIncorrectValue)
                return false
            }

            playerCache.setSpeed(args[0].toInt())
            s.sendMessage(GeneralLang.speedSendSuccess.replace("%value%", args[0]))


            return false
        }

        if (args.size != 2) return true

        //check perm
        if (s is Player && !s.hasPermission("essentialsk.commands.speed.other")) {
            s.sendMessage(GeneralLang.generalNotPerm)
            return false
        }

        //check if player exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.generalPlayerNotOnline)
            return false
        }

        val playerCache = PlayerDataDAO[p] ?: return false

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            playerCache.clearSpeed()
            s.sendMessage(GeneralLang.speedSendRemoveOther.replace("%player%", p.name))
            p.sendMessage(GeneralLang.speedSendOtherRemove)
            return false
        }

        //check if number is 0-10
        if (args[1].toInt() > 10 || args[1].toInt() < 0) {
            s.sendMessage(GeneralLang.speedSendIncorrectValue)
            return false
        }

        playerCache.setSpeed(args[1].toInt())

        s.sendMessage(GeneralLang.speedSendSuccessOther.replace("%player%", p.name).replace("%value%", args[1]))
        p.sendMessage(GeneralLang.speedSendOtherSuccess.replace("%value%", args[1]))
        return false
    }
}
