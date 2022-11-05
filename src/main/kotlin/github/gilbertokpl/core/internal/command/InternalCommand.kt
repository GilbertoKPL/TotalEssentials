package github.gilbertokpl.core.internal.command

import github.gilbertokpl.core.external.command.CommandCreator
import github.gilbertokpl.core.external.command.CommandTarget
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


internal class InternalCommand(commandCreator: CommandCreator) {

    private val cc = commandCreator

    fun execute(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (s !is Player && cc.target == CommandTarget.PLAYER) {
            s.sendMessage(cc.basePlugin?.getConfig()?.messages()?.generalOnlyPlayerCommand!!)
            return true
        }

        if (s is Player && cc.target == CommandTarget.CONSOLE) {
            s.sendMessage(cc.basePlugin?.getConfig()?.messages()?.generalOnlyConsoleCommand!!)
            return true
        }

        if (s is Player && !cc.permission.isNullOrEmpty() && !s.hasPermission(cc.permission!!)) {
            s.sendMessage(cc.basePlugin?.getConfig()?.messages()?.generalNotPerm!!)
            return true
        }

        var error = false

        if (cc.maximumSize != null && args.size > cc.maximumSize!! || cc.minimumSize != null && args.size < cc.minimumSize!!) {
            error = true
        } else {
            if (cc.countdown != null && s is Player) {
                val time = cc.hashCountDown.getOrDefault(s, 0)

                if (time != 0L && System.currentTimeMillis() < time) {
                    s.sendMessage(
                        cc.basePlugin?.getConfig()?.messages()?.generalCooldownMoreTime!!.replace(
                            "%time%",
                            cc.basePlugin?.getTime()!!.convertMillisToString(time - System.currentTimeMillis(), true)
                        )
                    )
                    return true
                }
            }
            if (cc.funCommand(s, label, args)) {
                error = true
            } else {
                if (cc.countdown != null && s is Player) {
                    cc.hashCountDown[s] = System.currentTimeMillis() + (cc.countdown!! * 1000)
                }
            }
        }

        if (error) {
            s.sendMessage(cc.basePlugin?.getConfig()?.messages()?.generalCommandsUsage!!)
            for (it in cc.commandUsage) {
                val to = it.split("_")
                if (to.size == 1) {
                    s.sendMessage(
                        cc.basePlugin?.getConfig()!!.messages().generalCommandsUsageList!!.replace(
                            "%command%",
                            it
                        )
                    )
                    continue
                }
                if (to[0] == "C" && s is Player || to[0] == "P" && s !is Player) {
                    continue
                }
                if (s !is Player || s.hasPermission(to[0])) {
                    s.sendMessage(
                        cc.basePlugin?.getConfig()?.messages()!!.generalCommandsUsageList!!.replace(
                            "%command%",
                            to[1]
                        )
                    )
                }
            }
        }

        return true
    }
}