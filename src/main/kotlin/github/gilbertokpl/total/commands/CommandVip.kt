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
import github.gilbertokpl.total.util.PlayerUtil
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
            maximumSize = null,
            usage = listOf(
                "totalessentials.commands.vip.admin_/vip list",
                "totalessentials.commands.vip.admin_/vip criar <vipName> <vipGroup>",
                "totalessentials.commands.vip.admin_/vip discrole <vipName> <roleID>",
                "totalessentials.commands.vip.admin_/vip gerarkey <vipName> <days>",
                "totalessentials.commands.vip.admin_/vip dar <player> <vipName> <days> <items,true/false>",
                "totalessentials.commands.vip.admin_/vip tempo <player>",
                "totalessentials.commands.vip.admin_/vip itens <vipName>",
                "totalessentials.commands.vip.admin_/vip comando <vipName> list",
                "totalessentials.commands.vip.admin_/vip comando <vipName> add <command>",
                "totalessentials.commands.vip.admin_/vip comando <vipName> remove <command>",
                "/vip usarkey <key>",
                "/vip items",
                "/vip tempo",
                "/vip mudar",
                "/vip discord <discordID>",
                "/vip token <token>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args[0].lowercase() == "criar" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            if (VipData.vipExists(args[1])) {
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

        if (args[0].lowercase() == "list" && args.size == 1 && s.hasPermission("totalessentials.commands.vip.admin")) {
            s.sendMessage(LangConfig.VipsListMessage)
            for (i in VipData.vipPrice.getMap()) {
                s.sendMessage(LangConfig.VipsList.replace("%vip%", i.key))
            }
            return false
        }

        if (args[0].lowercase() == "gerarkey" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val time = args[2].toLongOrNull() ?: return true

            if (VipData.vipExists(args[1])) {
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

            val vipItems = VipData.vipItems[vipName]!!

            if ((54 - (PlayerData.vipItems[s]?.size ?: 0)) <= vipItems.size.toLong()) {
                s.sendMessage(LangConfig.VipsClearItemsInventory)
                return false
            }

            val vipTime = KeyData.vipTime[args[1]] ?: return true

            val millisVipTime = vipTime * 86400000 + System.currentTimeMillis()

            PlayerData.vipCache[s] = hashMapOf(vipName to millisVipTime)

            KeyData.remove(args[1])

            VipUtil.updateCargo(s.name.lowercase(), vipName)

            PlayerData.vipItems[s] = vipItems

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", vipName).replace("%days%", vipTime.toString()))

            Discord.sendDiscordMessage(
                LangConfig.VipsDiscordActivateMessage
                    .replace("%player%", args[1])
                    .replace(
                        "%time%",
                        TotalEssentials.basePlugin.getTime().convertMillisToString(vipTime * 86400000, false)
                    )
                    .replace("%vip%", args[2]),
                true
            )

            PlayerUtil.sendAllMessage(
                LangConfig.VipsActivateMessage
                    .replace("%player%", args[1])
                    .replace(
                        "%time%",
                        TotalEssentials.basePlugin.getTime().convertMillisToString(vipTime * 86400000, false)
                    )
                    .replace("%vip%", args[2])
            )

            return false

        }

        if (args[0].lowercase() == "discrole" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {

            val role = args[2].toLongOrNull() ?: return false

            if (!VipData.vipExists(args[1])) {
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

        if (args[0].lowercase() == "comando" && args.size >= 3 && s.hasPermission("totalessentials.commands.vip.admin")) {

            if (!VipData.vipExists(args[1])) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (args[2].lowercase() == "add" && args.size > 3) {

                val msg = StringBuilder()
                for (arg in args) {
                    msg.append(arg).append(" ")
                }

                VipData.vipCommands[args[1]] = arrayListOf(msg.split("add ")[1])
                s.sendMessage(LangConfig.VipsCommandsAdd)
                return false
            }

            if (args[2].lowercase() == "remove" && args.size > 3) {
                val msg = StringBuilder()
                for (arg in args) {
                    msg.append(arg).append(" ")
                }

                VipData.vipCommands.remove(args[1], msg.split("remove ")[1])
                s.sendMessage(LangConfig.VipsCommandsRemove)
                return false
            }

            if (args[2].lowercase() == "list") {
                s.sendMessage(LangConfig.VipsCommandsListMessage)
                for (c in VipData.vipCommands[args[1]]!!) {
                    s.sendMessage(LangConfig.VipsCommandsList.replace("%command%", c))
                }
                return false
            }

        }

        if (s is Player && args[0].lowercase() == "itens" && args.size == 2 && s.hasPermission("totalessentials.commands.vip.admin") ||
            s is Player && args[0].lowercase() == "items" && args.size == 2 && s.hasPermission("totalessentials.commands.vip.admin")
        ) {

            if (!VipData.vipExists(args[1])) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            val inv = TotalEssentials.instance.server.createInventory(null, 54, "§eVipEditItens " + args[1])

            for (i in VipData.vipItems[args[1]]!!) {
                inv.addItem(i)
            }

            DataManager.playerVipEdit[s] = args[1]

            s.openInventory(inv)
            return false
        }

        if (s is Player && args[0].lowercase() == "itens" && args.size == 1 ||
            s is Player && args[0].lowercase() == "items" && args.size == 1
        ) {

            val inv = TotalEssentials.instance.server.createInventory(null, 54, "§eVipItens")

            for (i in PlayerData.vipItems[s] ?: emptyList()) {
                inv.addItem(i)
            }

            s.openInventory(inv)

            return false
        }

        if (args[0].lowercase() == "tempo" && args.size == 2 && s.hasPermission("totalessentials.commands.vip.admin")) {

            if (!PlayerData.checkIfPlayerExist(args[1])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            val cache = PlayerData.vipCache[args[1]]

            if (cache.isNullOrEmpty()) {
                s.sendMessage(LangConfig.VipsTimeNoVip)
                return false
            }
            s.sendMessage(LangConfig.VipsTimeFirstOtherMessage.replace("%player%", args[1]))
            for (i in cache) {
                s.sendMessage(
                    LangConfig.VipsTimeMessage.replace("%vipName%", i.key).replace(
                        "%vipTime%",
                        TotalEssentials.basePlugin.getTime()
                            .convertMillisToString(i.value - System.currentTimeMillis(), false)
                    )
                )
            }
            return false
        }

        if (args.size == 1 && args[0].lowercase() == "tempo" && s is Player) {
            val cache = PlayerData.vipCache[s]

            if (cache.isNullOrEmpty()) {
                s.sendMessage(LangConfig.VipsTimeNoVip)
                return false
            }

            s.sendMessage(LangConfig.VipsTimeFirstMessage)

            for (i in cache) {
                s.sendMessage(
                    LangConfig.VipsTimeMessage.replace("%vipName%", i.key).replace(
                        "%vipTime%",
                        TotalEssentials.basePlugin.getTime()
                            .convertMillisToString(i.value - System.currentTimeMillis(), false)
                    )
                )
            }
            return false
        }

        if (args[0].lowercase() == "mudar" && args.size == 1 && s is Player) {
            val vipName = VipUtil.updateCargo(s.name.lowercase()) ?: return false
            s.sendMessage(LangConfig.VipsSwitch.replace("%vipName%", vipName))
            return false
        }

        if (args[0].lowercase() == "dar" && args.size == 5 && s.hasPermission("totalessentials.commands.vip.admin")) {

            if (args[4].toBooleanStrictOrNull() == null) {
                return true
            }

            if (args[3].toLongOrNull() == null) {
                return true
            }

            if (!VipData.vipExists(args[2])) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (!PlayerData.checkIfPlayerExist(args[1])) {
                PlayerData.createNewPlayerData(args[1].lowercase())
            }

            if ((54 - (PlayerData.vipItems[args[1]]?.size ?: 0)) <= (VipData.vipItems[args[2]]?.size?.toLong() ?: 0)) {
                s.sendMessage(LangConfig.VipsClearItemsInventory)
                return false
            }

            val mv = PlayerData.vipCache[args[1]]?.get(args[2]) ?: 0L

            val millisVipTime = if (mv == 0L) {
                (args[3].toLong() * 86400000) + System.currentTimeMillis()
            } else {
                mv + args[3].toLong() * 86400000
            }

            PlayerData.vipCache[args[1]] = hashMapOf(args[2] to millisVipTime)

            if (args[4].toBoolean()) {
                PlayerData.vipItems[args[1]] = VipData.vipItems[args[2]]!!
            }

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", args[2]).replace("%days%", args[3]))

            PlayerUtil.sendAllMessage(
                LangConfig.VipsActivateMessage
                    .replace("%player%", args[1])
                    .replace(
                        "%time%",
                        TotalEssentials.basePlugin.getTime().convertMillisToString(args[3].toLong() * 86400000, false)
                    )
                    .replace("%vip%", args[2])
            )

            VipUtil.updateCargo(args[1], args[2], args[4].toBoolean())

            Discord.sendDiscordMessage(
                LangConfig.VipsDiscordActivateMessage
                    .replace("%player%", args[1])
                    .replace(
                        "%time%",
                        TotalEssentials.basePlugin.getTime().convertMillisToString(args[3].toLong() * 86400000, false)
                    )
                    .replace("%vip%", args[2]),
                true
            )

            return false
        }

        if (args[0].lowercase() == "discord" && args.size == 2 && s is Player) {
            val id = args[1].toLongOrNull() ?: return true
            TotalEssentials.basePlugin.getTask().async {
                val token = KeyData.generateRandomString()
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
            val id = DataManager.tokenVip[args[1]] ?: run {
                s.sendMessage(LangConfig.VipsDiscordTokenError)
                return false
            }
            s.sendMessage(LangConfig.VipsDiscordTokenActivate)

            if (PlayerData.discordCache[s] != 0L) {
                for (v in VipData.vipDiscord.getMap()) {
                    Discord.removeUserRole(PlayerData.discordCache[s]!!, v.value ?: 0)
                }
            }

            PlayerData.discordCache[s] = id

            for (v in VipData.vipDiscord.getMap()) {
                Discord.removeUserRole(id, v.value ?: continue)
            }

            for (v in PlayerData.vipCache[s]!!) {
                Discord.addUserRole(id, VipData.vipDiscord[v.key] ?: continue)
            }

            DataManager.tokenVip.remove(args[1])

            return false
        }

        return true
    }
}