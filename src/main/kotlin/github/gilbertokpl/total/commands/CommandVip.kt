package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataManager
import github.gilbertokpl.total.cache.local.KeyData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.VipData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.VipUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVip : github.gilbertokpl.core.external.command.CommandCreator("vip") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("vips"),
            active = MainConfig.vipActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.vip",
            minimumSize = 1,
            maximumSize = 4,
            usage = listOf(
                "totalessentials.commands.vip.admin_/vip criar <vipName> <vipGroup>",
                "totalessentials.commands.vip.admin_/vip discrole <vipName> <roleID>",
                "totalessentials.commands.vip.admin_/vip gerarkey <vipName> <days>",
                "/vip usarkey <key>",
                "totalessentials.commands.vip.admin_/vip dar <player> <vipName> <days>",
                "totalessentials.commands.vip.admin_/vip tempo <player>",
                "P_/vip tempo",
                "P_/vip mudar",
                "P_/vip discord <discordID>",
                "P_/vip token <token>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args[0].lowercase() == "criar" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin"))  {
            if (VipData.checkIfVipExist(args[1])) {
                s.sendMessage(LangConfig.VipsExist)
                return false
            }
            if (TotalEssentials.permission.groups.contains(args[2])) {
                VipData.createNewVip(args[1], args[2])
                s.sendMessage(LangConfig.VipsCreateNew.replace("%vip%", args[1]))
                return false
            }
            s.sendMessage(LangConfig.VipsGroupNotExist)
            return false
        }

        if (args[0].lowercase() == "gerarkey" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val time = args[2].toLongOrNull() ?: return true

            if (VipData.checkIfVipExist(args[1])) {
                val key = KeyData.genNewVipKey(args[1], time)
                s.sendMessage(LangConfig.VipsCreateNewKey.replace("%key%", key))
            }
            return false
        }

        if (s is Player && args[0].lowercase() == "usarkey" && args.size == 2) {
            if (!KeyData.checkIfKeyExist(args[1])) {
                s.sendMessage(LangConfig.VipsKeyNotExist)
                return false
            }

            val vipName = KeyData.vipName[args[1]] ?: return true
            val vipTime = KeyData.vipTime[args[1]] ?: return true

            val millisVipTime = vipTime * 86400000 + System.currentTimeMillis()

            PlayerData.vipCache[s] = hashMapOf(vipName to millisVipTime)

            KeyData.delete(args[1])

            VipUtil.updateCargo(s.name.lowercase(), VipData.vipGroup[vipName])

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", vipName).replace("%days%", vipTime.toString()))

            return false

        }

        if (args[0].lowercase() == "discrole" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {

            val role = args[2].toLongOrNull() ?: return false

            if (!VipData.checkIfVipExist(args[1])) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (!Discord.checkIfRoleIdExist(role)) {
                s.sendMessage(LangConfig.VipsDiscordRoleError)
                return false
            }

            VipData.vipDiscord[args[1]] = role
            s.sendMessage(LangConfig.VipsDiscordRoleActivate)
            return false

        }

        if (args[0].lowercase() == "tempo" && args.size == 2 && s.hasPermission("totalessentials.commands.vip.admin")) {
            for (i in PlayerData.vipCache[args[1]] ?: return false) {
                s.sendMessage(LangConfig.VipsTimeMessage.replace("%vipName%", i.key).replace("%vipTime%", TotalEssentials.basePlugin.getTime().convertMillisToString(i.value - System.currentTimeMillis(), false)))
            }
            return false
        }

        if (args.size == 1 && args[0].lowercase() == "tempo" && s is Player) {
            for (i in PlayerData.vipCache[s] ?: return false) {
                s.sendMessage(LangConfig.VipsTimeMessage.replace("%vipName%", i.key).replace("%vipTime%", TotalEssentials.basePlugin.getTime().convertMillisToString(i.value - System.currentTimeMillis(), false)))
            }
            return false
        }

        if (args[0].lowercase() == "mudar" && args.size == 1 && s is Player) {
            val vipName = VipUtil.updateCargo(s.name.lowercase()) ?: return false
            s.sendMessage(LangConfig.VipsSwitch.replace("%vipName%", vipName))
            return false
        }

        if (args[0].lowercase() == "dar" && args.size == 4 && s.hasPermission("totalessentials.commands.vip.admin")) {

            if (args[3].toLongOrNull() == null) {
                return true
            }

            if (!PlayerData.checkIfPlayerExist(args[1])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (!VipData.checkIfVipExist(args[2])) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            val millisVipTime = (args[3].toLong() * 86400000) + System.currentTimeMillis()

            PlayerData.vipCache[args[1]] = hashMapOf(args[2] to millisVipTime)

            VipUtil.updateCargo(s.name.lowercase(), VipData.vipGroup[args[2]])

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", args[2]).replace("%days%", args[3]))
            return false
        }

        if (args[0].lowercase() == "discord" && args.size == 2 && s is Player) {
            val id = args[1].toLongOrNull() ?: return true
            TotalEssentials.basePlugin.getTask().async {
                val token = KeyData.getRandomString()
                if (Discord.sendDiscordMessage(id, LangConfig.VipsDiscordMessage.replace("%value%", token))) {
                    DataManager.tokenVip[token] = id
                    s.sendMessage(LangConfig.VipsDiscordLocalMessage)
                } else {
                    s.sendMessage(LangConfig.VipsDiscordUserIdNotExist)
                }
            }
            return false
        }

        if (args[0].lowercase() == "token" && args.size == 2 && s is Player) {
            val token = DataManager.tokenVip[args[1]] ?: run {
                s.sendMessage(LangConfig.VipsDiscordTokenError)
                return false
            }
            s.sendMessage(LangConfig.VipsDiscordTokenActivate)

            if (PlayerData.discordCache[s] != 0L) {
                for (v in VipData.vipDiscord.getMap()) {
                    Discord.removeUserRole(PlayerData.discordCache[s]!!, v.value ?: 0)
                }
            }

            PlayerData.discordCache[s] = token

            for (v in VipData.vipDiscord.getMap()) {
                Discord.removeUserRole(token, v.value ?: continue)
            }

            for (v in PlayerData.vipCache[s]!!) {
                Discord.addUserRole(token, VipData.vipDiscord[v.key] ?: continue)
            }
        }

        return true
    }
}