package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.PluginUtil
import org.bukkit.command.CommandSender

class CommandTotal : github.gilbertokpl.core.external.command.CommandCreator("total") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("system", "essentials", "s", "ks", "e"),
            active = true,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.total",
            minimumSize = 1,
            maximumSize = 3,
            usage = listOf(
                "/total reload",
                "/total host",
                "/total plugin <load/unload/reload> <pluginName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

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
            if (github.gilbertokpl.total.TotalEssentials.basePlugin.reloadConfig()) {
                s.sendMessage(
                    LangConfig.generalConfigReload
                )
            }
            return false
        }

        if (args[0].lowercase() == "host") {
            s.sendMessage(LangConfig.generalHostWait)
            //sendhostinfo
            val host = basePlugin!!.getHost().getHost()
            LangConfig.generalHostConfigInfo.forEach {
                s.sendMessage(
                    it.replace("%ip%", host.ipAddress)
                        .replace("%os%", host.osName)
                        .replace("%os_version%", host.osVersion)
                        .replace("%cpu_name%", host.cpuName)
                        .replace("%cpu_clock_min%", host.cpuClockMin)
                        .replace("%cpu_clock_max%", host.cpuClockMax)
                        .replace("%cores%", host.cpuCores)
                        .replace("%cores_server%", host.cpuAvailable)
                        .replace(
                            "%cpu_usage%", host.cpuUsage
                        )
                        .replace("%used_mem%", host.memoryAllUsage)
                        .replace("%used_server_mem%", host.memoryServerUsage)
                        .replace("%max_mem%", host.memoryMax)
                        .replace("%max_server_mem%", host.memoryServerMax)
                        .replace("%gpu%", host.gpuName)
                        .replace("%name_hd%", host.diskName)
                        .replace("%used_hd%", host.diskUsage)
                        .replace("%max_hd%", host.diskMax)
                )
            }
            return false
        }
        return true
    }
}
