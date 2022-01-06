package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpeed : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "speed"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.speed"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage = listOf(
        "/speed <value>",
        "/speed remove",
        "cardinal.commands.speed.other_/speed <player> <value>",
        "cardinal.commands.speed.other_/speed <player> remove"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if is 1
        if (args.size == 1 && s is Player) {

            val playerCache = DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                playerCache.clearSpeed()
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendRemove)
                return false
            }

            //check int
            try {
                args[0].toInt()
            } catch (e: Exception) {
                return true
            }

            //check if number is 0-10
            if (args[0].toInt() > 10 || args[0].toInt() < 0) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendIncorrectValue)
                return false
            }

            playerCache.setSpeed(args[0].toInt())
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendSuccess.replace(
                    "%value%",
                    args[0]
                )
            )


            return false
        }

        if (args.size != 2) return true

        //check perm
        if (s is Player && !s.hasPermission("cardinal.commands.speed.other")) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
            return false
        }

        //check if player exist
        val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        val playerCache = DataManager.getInstance().playerCacheV2[p.name.lowercase()] ?: return false

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            playerCache.clearSpeed()
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendRemoveOther.replace(
                    "%player%",
                    p.name
                )
            )
            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendOtherRemove)
            return false
        }

        //check if number is 0-10
        if (args[1].toInt() > 10 || args[1].toInt() < 0) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendIncorrectValue)
            return false
        }

        playerCache.setSpeed(args[1].toInt())

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendSuccessOther.replace(
                "%player%",
                p.name
            ).replace("%value%", args[1])
        )
        p.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().speedSendOtherSuccess.replace(
                "%value%",
                args[1]
            )
        )
        return false
    }
}