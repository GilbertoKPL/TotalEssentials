package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.local.KeyData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.VipData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.VipUtil
import net.milkbowl.vault.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Handles all VIP-related commands.
 * Includes admin commands (create/remove VIPs) and player commands (use VIP keys, view time, manage items).
 */
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
                "totalessentials.commands.vip.admin_/vip remover <player> <vipName>",
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
                "/vip token <token>",
                "C_/vip timeadd <days>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true

        val subCommand = args[0].lowercase()
        val perm = TotalEssentialsJava.getPermission()

        /**
         * ------------------------------
         * ADMIN VIP COMMANDS
         * ------------------------------
         */

        // CREATE VIP
        if (subCommand == "criar" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            val vipGroup = args[2]

            if (VipData.vipExists(vipName)) {
                s.sendMessage(LangConfig.VipsExist)
                return false
            }

            val groupExists = when (perm) {
                is Permission -> perm.groups.contains(vipGroup)
                is net.milkbowl.vault2.permission.Permission -> perm.groups.contains(vipGroup)
                else -> false
            }

            if (groupExists) {
                VipData.createNewVip(vipName, vipGroup)
                s.sendMessage(LangConfig.VipsCreateNew.replace("%vip%", vipName))
            } else {
                s.sendMessage(LangConfig.VipsGroupNotExist)
            }
            return false
        }

        // LIST VIPs
        if (subCommand == "list" && args.size == 1 && s.hasPermission("totalessentials.commands.vip.admin")) {
            s.sendMessage(LangConfig.VipsListMessage)
            VipData.vipPrice.getMap().forEach { (vip, _) ->
                s.sendMessage(LangConfig.VipsList.replace("%vip%", vip))
            }
            return false
        }

        // REMOVE VIP from a player
        if (subCommand == "remover" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val playerName = args[1]
            val vipName = args[2]

            if (!PlayerData.checkIfPlayerExists(playerName)) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (!VipData.vipExists(vipName)) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            val cache = PlayerData.vipCache[playerName] ?: return true
            if (cache[vipName] == null) {
                s.sendMessage(LangConfig.VipsRemoveNoVip)
                return false
            }

            cache.remove(vipName)
            VipUtil.updateCargo(playerName)

            when (perm) {
                is Permission -> perm.playerRemoveGroup(VipUtil.world, playerName, VipData.vipGroup[vipName])
                is net.milkbowl.vault2.permission.Permission -> perm.playerRemoveGroup(VipUtil.world, playerName, VipData.vipGroup[vipName])
            }

            VipData.vipQuantity[vipName] = (VipData.vipQuantity[vipName] ?: 0) - 1

            PlayerData.discordCache[playerName]?.let { discordId ->
                VipData.vipDiscord[vipName]?.let { roleId ->
                    Discord.removeUserRole(discordId, roleId)
                }
            }

            s.sendMessage(LangConfig.VipsRemove)
            return false
        }

        // GENERATE VIP KEY
        if (subCommand == "gerarkey" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            val days = args[2].toLongOrNull() ?: return true

            if (VipData.vipExists(vipName)) {
                val key = KeyData.genNewVipKey(vipName, days)
                s.sendMessage(LangConfig.VipsCreateNewKey.replace("%key%", key))
            }
            return false
        }

        // ASSIGN VIP to player with optional items
        if (subCommand == "dar" && args.size == 5 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val playerName = args[1]
            val vipName = args[2]
            val days = args[3].toLongOrNull() ?: return true
            val giveItems = args[4].toBooleanStrictOrNull() ?: return true

            if (!VipData.vipExists(vipName)) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (!PlayerData.checkIfPlayerExists(playerName)) {
                PlayerData.createNewPlayerData(playerName.lowercase())
            }

            val existingItems = PlayerData.vipItems[playerName]?.size ?: 0
            val vipItemSize = VipData.vipItems[vipName]?.size ?: 0
            if ((90 - existingItems) <= vipItemSize) {
                s.sendMessage(LangConfig.VipsClearItemsInventory)
                return false
            }

            val currentVipTime = PlayerData.vipCache[playerName]?.get(vipName) ?: 0L
            val millisVipTime = if (currentVipTime == 0L) System.currentTimeMillis() + days * 86_400_000 else currentVipTime + days * 86_400_000

            PlayerData.vipCache[playerName] = hashMapOf(vipName to millisVipTime)

            if (giveItems) PlayerData.vipItems[playerName] = VipData.vipItems[vipName]!!

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", vipName).replace("%days%", days.toString()))

            PlayerUtil.sendAllMessage(
                LangConfig.VipsActivateMessage
                    .replace("%player%", playerName)
                    .replace("%time%", TotalEssentialsJava.getBasePlugin().getTime().convertMillisToString(days * 86_400_000, false))
                    .replace("%vip%", vipName)
            )

            VipUtil.updateCargo(playerName, vipName, giveItems)

            Discord.sendDiscordMessage(
                LangConfig.VipsDiscordActivateMessage
                    .replace("%player%", playerName)
                    .replace("%time%", TotalEssentialsJava.getBasePlugin().getTime().convertMillisToString(days * 86_400_000, false))
                    .replace("%vip%", vipName),
                true
            )

            return false
        }

        // ASSIGN Discord role to VIP
        if (subCommand == "discrole" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            val roleId = args[2].toLongOrNull() ?: return false

            if (!VipData.vipExists(vipName)) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (!Discord.checkIfRoleIdExist(roleId)) {
                s.sendMessage(LangConfig.VipsDiscordRoleError)
                return false
            }

            VipData.vipDiscord[vipName] = roleId
            s.sendMessage(LangConfig.VipsDiscordRoleActivate)
            return false
        }

        // MANAGE VIP commands (add/remove/list)
        if (subCommand == "comando" && args.size >= 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            if (!VipData.vipExists(vipName)) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            when (args[2].lowercase()) {
                "add" -> {
                    val command = args.drop(3).joinToString(" ")
                    VipData.vipCommands[vipName] = arrayListOf(command)
                    s.sendMessage(LangConfig.VipsCommandsAdd)
                }
                "remove" -> {
                    val command = args.drop(3).joinToString(" ")
                    VipData.vipCommands.remove(vipName, command)
                    s.sendMessage(LangConfig.VipsCommandsRemove)
                }
                "list" -> {
                    s.sendMessage(LangConfig.VipsCommandsListMessage)
                    VipData.vipCommands[vipName]?.forEach { cmd ->
                        s.sendMessage(LangConfig.VipsCommandsList.replace("%command%", cmd))
                    }
                }
            }
            return false
        }

        /**
         * ------------------------------
         * PLAYER VIP COMMANDS
         * ------------------------------
         */

        // VIEW or EDIT VIP ITEMS
        if (s is Player && (subCommand == "itens" || subCommand == "items")) {
            val vipName = args.getOrNull(1)

            val inventory = if (vipName != null && s.hasPermission("totalessentials.commands.vip.admin")) {
                if (!VipData.vipExists(vipName)) {
                    s.sendMessage(LangConfig.VipsNotExist)
                    return false
                }
                val inv = TotalEssentialsJava.getInstance().server.createInventory(null, 54, "§eVipEditItens $vipName")
                VipData.vipItems[vipName]?.forEach { inv.addItem(it) }
                Data.playerVipEdit[s] = vipName
                inv
            } else {
                val playerItems = PlayerData.vipItems[s] ?: emptyList()
                val size = if (playerItems.size > 54) 90 else 54
                val inv = TotalEssentialsJava.getInstance().server.createInventory(null, size, "§eVipItens")
                playerItems.forEach { inv.addItem(it) }
                inv
            }

            s.openInventory(inventory)
            return false
        }

        // VIEW VIP TIME
        if (subCommand == "tempo") {
            if (s is Player && args.size == 1) {
                val cache = PlayerData.vipCache[s]
                if (cache.isNullOrEmpty()) {
                    s.sendMessage(LangConfig.VipsTimeNoVip)
                    return false
                }
                s.sendMessage(LangConfig.VipsTimeFirstMessage)
                cache.forEach { (vip, time) ->
                    s.sendMessage(LangConfig.VipsTimeMessage.replace("%vipName%", vip).replace("%vipTime%", TotalEssentialsJava.getBasePlugin().getTime().convertMillisToString(time - System.currentTimeMillis(), false)))
                }
            } else if (args.size == 2 && s.hasPermission("totalessentials.commands.vip.admin")) {
                val targetPlayer = args[1]
                if (!PlayerData.checkIfPlayerExists(targetPlayer)) {
                    s.sendMessage(LangConfig.generalPlayerNotExist)
                    return false
                }
                val cache = PlayerData.vipCache[targetPlayer]
                if (cache.isNullOrEmpty()) {
                    s.sendMessage(LangConfig.VipsTimeNoVip)
                    return false
                }
                s.sendMessage(LangConfig.VipsTimeFirstOtherMessage.replace("%player%", targetPlayer))
                cache.forEach { (vip, time) ->
                    s.sendMessage(LangConfig.VipsTimeMessage.replace("%vipName%", vip).replace("%vipTime%", TotalEssentialsJava.getBasePlugin().getTime().convertMillisToString(time - System.currentTimeMillis(), false)))
                }
            }
            return false
        }

        // SWITCH VIP
        if (subCommand == "mudar" && s is Player && args.size == 1) {
            val vipName = VipUtil.updateCargo(s.name.lowercase()) ?: return false
            s.sendMessage(LangConfig.VipsSwitch.replace("%vipName%", vipName))
            return false
        }

        // DISCORD COMMANDS (linking and token usage)
        if (s is Player) {
            when (subCommand) {
                "discord" -> {
                    val discordId = args.getOrNull(1)?.toLongOrNull() ?: return true
                    TotalEssentialsJava.getBasePlugin().getTask().async {
                        val token = KeyData.generateRandomString()
                        if (Discord.sendDiscordMessage(discordId, LangConfig.VipsDiscordMessage.replace("%value%", token))) {
                            Data.tokenVip[token] = discordId
                            s.sendMessage(LangConfig.VipsDiscordLocalMessage)
                        } else s.sendMessage(LangConfig.VipsDiscordUserIdNotExist)
                    }
                    return false
                }
                "token" -> {
                    val token = args.getOrNull(1) ?: return true
                    val discordId = Data.tokenVip[token] ?: run {
                        s.sendMessage(LangConfig.VipsDiscordTokenError)
                        return false
                    }
                    s.sendMessage(LangConfig.VipsDiscordTokenActivate)

                    // Remove previous roles
                    PlayerData.discordCache[s]?.let { oldId ->
                        VipData.vipDiscord.getMap().forEach { (_, role) ->
                            Discord.removeUserRole(oldId, role ?: return@forEach)
                        }
                    }

                    PlayerData.discordCache[s] = discordId

                    // Remove all VIP roles and add new ones
                    VipData.vipDiscord.getMap().forEach { (_, role) ->
                        Discord.removeUserRole(discordId, role ?: return@forEach)
                    }
                    PlayerData.vipCache[s]?.forEach { (vip, _) ->
                        Discord.addUserRole(discordId, VipData.vipDiscord[vip] ?: return@forEach)
                    }

                    Data.tokenVip.remove(token)
                    return false
                }
            }
        }

        // ADMIN ONLY: ADD TIME TO ALL VIPs
        if (subCommand == "timeadd" && s !is Player && args.size == 2) {
            val addTime = args[1].toLongOrNull()?.times(86_400_000) ?: return true
            PlayerData.vipCache.getMap().forEach { (playerName, vipMap) ->
                vipMap?.forEach { (vipName, time) ->
                    PlayerData.vipCache[playerName]?.set(vipName, time + addTime)
                }
            }

            TotalEssentialsJava.getBasePlugin().getTask().async {
                try {
                    transaction(basePlugin?.sql) {
                        for (i in basePlugin?.getCache()?.toByteUpdate!!) {
                            try { i.update() } catch (_: Exception) {}
                        }
                    }
                } catch (_: Exception) {}
            }
            return false
        }

        /**
         * ------------------------------
         * IF COMMAND DOES NOT MATCH
         * ------------------------------
         */
        return true
    }
}
