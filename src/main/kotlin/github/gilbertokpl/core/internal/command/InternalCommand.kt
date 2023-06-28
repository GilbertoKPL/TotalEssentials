package github.gilbertokpl.core.internal.command

import github.gilbertokpl.core.external.command.CommandCreator
import github.gilbertokpl.core.external.command.CommandTarget
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


internal class InternalCommand(private val commandCreator: CommandCreator) {

    fun execute(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val configMessages = commandCreator.basePlugin?.getConfig()?.messages()

        if (s !is Player && commandCreator.target == CommandTarget.PLAYER) {
            s.sendMessage(configMessages?.generalOnlyPlayerCommand!!)
            return true
        }

        if (s is Player && commandCreator.target == CommandTarget.CONSOLE) {
            s.sendMessage(configMessages?.generalOnlyConsoleCommand!!)
            return true
        }

        if (s is Player && !commandCreator.permission.isNullOrEmpty() && !s.hasPermission(commandCreator.permission!!)) {
            s.sendMessage(configMessages?.generalNotPerm!!)
            return true
        }

        var error = false

        if (commandCreator.maximumSize != null && args.size > commandCreator.maximumSize!! ||
            commandCreator.minimumSize != null && args.size < commandCreator.minimumSize!!
        ) {
            error = true
        } else {
            if (commandCreator.countdown != null && s is Player) {
                val time = commandCreator.hashCountDown.getOrDefault(s, 0)

                if (time != 0L && System.currentTimeMillis() < time) {
                    val timeString = commandCreator.basePlugin?.getTime()
                        ?.convertMillisToString(time - System.currentTimeMillis(), true)
                    s.sendMessage(
                        configMessages?.generalCooldownMoreTime!!.replace("%time%", timeString!!)
                    )
                    return true
                }
            }
            if (commandCreator.funCommand(s, label, args)) {
                error = true
            } else {
                if (commandCreator.countdown != null && s is Player) {
                    commandCreator.hashCountDown[s] = System.currentTimeMillis() + (commandCreator.countdown!! * 1000)
                }
            }
        }

        if (error) {
            val sb = StringBuilder()
            sb.append(configMessages?.generalCommandsUsage!!).append("\n")
            for (it in commandCreator.commandUsage) {
                val to = it.split("_")
                if (to.size == 1) {
                    sb.append(
                        configMessages.generalCommandsUsageList!!.replace("%command%", it)
                    ).append("\n")
                    continue
                }
                if ((to[0] == "C" && s is Player) || (to[0] == "P" && s !is Player)) {
                    continue
                }
                if (s !is Player || s.hasPermission(to[0])) {
                    sb.append(
                        configMessages.generalCommandsUsageList!!.replace("%command%", to[1])
                    ).append("\n")
                }
            }
            s.sendMessage(sb.toString())
        }

        return true
    }
}