package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.ConfigUtil
import github.gilbertokpl.essentialsk.util.HostUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEssentialsK : CommandCreator {
    override val consoleCanUse: Boolean = true
    override val commandName = "essentialsk"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.essentialsk"
    override val minimumSize = 1
    override val maximumSize = 3
    override val commandUsage = listOf("/essentialsk reload", "/essentialsk host", "/essentialsk plugin <load/unload/reload> <pluginName>")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args[0].lowercase() == "plugin") {
            if (args.size == 2) return true

            val pl = PluginUtil.getPluginByName(args[2]) ?: run {
                return true
            }

            when (args[1]) {
                "load" -> PluginUtil.load(pl)
                "unload" -> PluginUtil.unload(pl)
                "reload" -> PluginUtil.reload(pl)
                else -> return true
            }
        }

        if (args[0].lowercase() == "reload") {
            if (ConfigUtil.reloadConfig(true)) {
                s.sendMessage(
                    GeneralLang.generalConfigReload
                )
            }
            return false
        }

        if (args[0].lowercase() == "host") {
            s.sendMessage(GeneralLang.generalHostWait)
            HostUtil.sendHostInfo(s)
            return false
        }
        return true
    }
}
