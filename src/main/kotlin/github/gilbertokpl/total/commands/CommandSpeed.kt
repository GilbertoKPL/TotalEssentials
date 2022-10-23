package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpeed : github.gilbertokpl.base.external.command.CommandCreator("speed") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.speedActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "essentialsk.commands.speed",
            minimumSize = 1,
            maximumSize = 2,
            usage = listOf(
                "/speed <value>",
                "/speed remove",
                "essentialsk.commands.speed.other_/speed <player> <value>",
                "essentialsk.commands.speed.other_/speed <player> remove"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        //check if is 1
        if (args.size == 1 && s is Player) {

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                clearSpeed(s)
                s.sendMessage(LangConfig.speedRemove)
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
                s.sendMessage(LangConfig.speedIncorrectValue)
                return false
            }

            setSpeed(args[0].toInt(), s)
            s.sendMessage(
                LangConfig.speedSuccess.replace(
                    "%value%",
                    args[0]
                )
            )


            return false
        }

        if (args.size != 2) return true

        //check perm
        if (s is Player && !s.hasPermission("essentialsk.commands.speed.other")) {
            s.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        //check if player exist
        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            clearSpeed(p)
            s.sendMessage(
                LangConfig.speedRemoveOther.replace(
                    "%player%",
                    p.name
                )
            )
            p.sendMessage(LangConfig.speedOtherRemove)
            return false
        }

        //check if number is 0-10
        if (args[1].toInt() > 10 || args[1].toInt() < 0) {
            s.sendMessage(LangConfig.speedIncorrectValue)
            return false
        }

        setSpeed(args[1].toInt(), p)

        s.sendMessage(
            LangConfig.speedSuccessOther.replace(
                "%player%",
                p.name
            ).replace("%value%", args[1])
        )
        p.sendMessage(
            LangConfig.speedOtherSuccess.replace(
                "%value%",
                args[1]
            )
        )
        return false
    }

    private fun setSpeed(vel: Int, player: Player) {
        PlayerData.speedCache[player] = vel

        player.walkSpeed = (vel * 0.1).toFloat()
        player.flySpeed = (vel * 0.1).toFloat()
    }

    private fun clearSpeed(player: Player) {
        PlayerData.speedCache[player] = 1

        player.walkSpeed = 0.2F
        player.flySpeed = 0.1F
    }
}
