package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.ConfigManager
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.HostUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandEssentialsK : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = true,
            consoleCanUse = true,
            commandName = "essentialsk",
            timeCoolDown = null,
            permission = "essentialsk.commands.essentialsk",
            minimumSize = 1,
            maximumSize = 3,
            commandUsage = listOf(
                "/essentialsk reload",
                "/essentialsk host",
                "/essentialsk save",
                "/essentialsk plugin <load/unload/reload> <pluginName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args[0].lowercase() == "save") {
            CoroutineScope(BukkitDispatcher(async = true)).launch {
                s.sendMessage(LangConfig.generalSaveDataMessage)
                DataManager.save()
                s.sendMessage(LangConfig.generalSaveDataSuccess)
            }
            return false
        }

        if (args[0].lowercase() == "plugin") {
            if (args.size == 1 || args.size == 2) return true

            if (args[1].lowercase() == "load") {
                s.sendMessage(PluginUtil.load(args[2]))
                return false
            }

            val pl = PluginUtil.getPluginByName(args[2]) ?: run {
                s.sendMessage(LangConfig.generalPluginNotFound)
                return false
            }

            when (args[1].lowercase()) {
                "unload" -> s.sendMessage(PluginUtil.unload(pl))
                "reload" -> PluginUtil.reload(pl, s)
                else -> return true
            }
            return false
        }

        if (args[0].lowercase() == "reload") {
            if (ConfigManager.reloadConfig()) {
                s.sendMessage(
                    LangConfig.generalConfigReload
                )
            }
            return false
        }

        if (args[0].lowercase() == "host") {
            s.sendMessage(LangConfig.generalHostWait)
            HostUtil.sendHostInfo(s)
            return false
        }
        return true
    }
}
