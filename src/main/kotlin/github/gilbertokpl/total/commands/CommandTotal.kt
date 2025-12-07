package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.KeyData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.VipData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import github.gilbertokpl.total.util.PluginUtil
import github.gilbertokpl.total.vip.VipManager.checkVip
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CommandTotal : CommandManager("total") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("system", "essentials", "s", "ks", "e"),
            active = true,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.total",
            minimumSize = 1,
            maximumSize = 3,
            usage = listOf(
                "/total reload",
                "/total host",
                "/total plugin <load/unload/reload> <pluginName>",
                "C_/total reset",
                "P_/total id",
                "/total save"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()) return true

        when (args[0].lowercase()) {
            "plugin" -> {
                if (args.size < 3) return true
                when (args[1].lowercase()) {
                    "load" -> sender.sendMessage(PluginUtil.load(args[2]))
                    "unload", "reload" -> {
                        val pl = PluginUtil.getPluginByName(args[2]) ?: run {
                            sender.sendMessage(LangConfig.generalPluginNotFound)
                            return false
                        }
                        if (args[1].lowercase() == "unload") sender.sendMessage(PluginUtil.unload(pl))
                        else PluginUtil.reload(pl, sender)
                    }
                    else -> return true
                }
                return false
            }

            "reload" -> {
                if (TotalEssentials.getCore().reloadConfig()) {
                    sender.sendMessage(LangConfig.generalConfigReload)
                }
                return false
            }

            "reset" -> {
                if (sender is Player) return false // reset is console only
                if (args.size == 1) return resetGenerateToken(sender)
                if (args.size == 2) return resetExecute(args[1])
            }

            "host" -> {
                sendHostInfo(sender)
                return false
            }

            "id" -> {
                if (sender is Player) sender.sendMessage(sender.itemInHand.type.name.lowercase())
                return false
            }

            "save" -> {
                TotalEssentials.getCore().getCache().save()
                sender.sendMessage("Salvo!")
                return false
            }
        }
        return true
    }

    private fun resetGenerateToken(s: CommandSender): Boolean {
        if (MainConfig.generalResetList.firstOrNull() == "0") {
            s.sendMessage(LangConfig.generalResetMessageNotSet)
            return false
        }

        TotalEssentials.getCore().getTask().async {
            val token = KeyData.generateRandomString()
            for (idString in MainConfig.generalResetList) {
                val id = idString.toLongOrNull() ?: continue
                if (!DiscordManager.sendDiscordMessage(id, LangConfig.generalResetDiscordMessage.replace("%value%", token))) {
                    s.sendMessage(LangConfig.VipsDiscordUserIdNotExist)
                }
            }
            Data.tokenReset = token
        }

        s.sendMessage(LangConfig.generalResetMessage)
        return false
    }

    private fun resetExecute(providedToken: String): Boolean {
        if (!providedToken.contains(Data.tokenReset)) return false

        for ((playerName, vips) in PlayerData.vipCache.getMap()) {

            PlayerData.commandCache[playerName, ""] = true
            PlayerData.vipItems[playerName, ArrayList()] = true

            if (vips.isNullOrEmpty()) continue

            if (checkVip(playerName)) continue

            for ((vipName, _) in vips) {
                val vipItems = VipData.vipItems[vipName] ?: continue
                val commands = VipData.vipCommands[vipName]?.joinToString("-") { it.replace("%player%", playerName) } ?: ""
                PlayerData.commandCache[playerName, commands] = true
                PlayerData.vipItems[playerName, vipItems] = true
            }
        }

        TotalEssentials.getCore().getTask().async {
            try {
                transaction(TotalEssentials.getCore().sql) {
                    TotalEssentials.getCore().getCache().getBuilders().forEach {
                        try { it.update() } catch (e: Exception) { println(e) }
                    }
                }
            } catch (e: Exception) { println(e) }
        }

        Data.tokenReset = ""
        return false
    }

    private fun sendHostInfo(s: CommandSender) {
        s.sendMessage(LangConfig.generalHostWait)
        val host = TotalEssentials.getCore().getHost().getHost()
        LangConfig.generalHostConfig.forEach { line ->
            s.sendMessage(
                line.replace("%ip%", host.ipAddress)
                    .replace("%os%", host.osName)
                    .replace("%os_version%", host.osVersion)
                    .replace("%cpu_name%", host.cpuName)
                    .replace("%cpu_clock_min%", host.cpuClockMin)
                    .replace("%cpu_clock_max%", host.cpuClockMax)
                    .replace("%cores%", host.cpuCores)
                    .replace("%cores_server%", host.cpuAvailable)
                    .replace("%cpu_usage%", host.cpuUsage)
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
    }
}
