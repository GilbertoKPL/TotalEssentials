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
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.vip.VipManager
import net.milkbowl.vault.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CommandVip : CommandManager("vip") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("vips"),
            active = MainConfig.vipActivated,
            target = CommandTargetType.ALL,
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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true

        val subCommand = args[0].lowercase()
        val perm = TotalEssentials.getPermission()

        /**
         * ------------------------------
         * ADMIN VIP COMMANDS
         * ------------------------------
         */

        // CREATE VIP
        if (subCommand == "criar" && args.size == 3 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            val vipGroup = args[2]

            if (VipData.vipExists(vipName)) {
                sender.sendMessage(LangConfig.VipsExist)
                return false
            }

            val groupExists = when (perm) {
                is Permission -> perm.groups.contains(vipGroup)
                is net.milkbowl.vault2.permission.Permission -> perm.groups.contains(vipGroup)
                else -> false
            }

            if (groupExists) {
                VipData.createNewVip(vipName, vipGroup)
                sender.sendMessage(LangConfig.VipsCreateNew.replace("%vip%", vipName))
            } else {
                sender.sendMessage(LangConfig.VipsGroupNotExist)
            }
            return false
        }

        // LIST VIPs
        if (subCommand == "list" && args.size == 1 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            sender.sendMessage(LangConfig.VipsListMessage)
            VipData.vipPrice.getMap().forEach { (vip, _) ->
                sender.sendMessage(LangConfig.VipsList.replace("%vip%", vip))
            }
            return false
        }

        // REMOVE VIP from a player
        if (subCommand == "remover" && args.size == 3 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            val playerName = args[1]
            val vipName = args[2]

            if (!PlayerData.checkIfPlayerExists(playerName)) {
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (!VipData.vipExists(vipName)) {
                sender.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            val cache = PlayerData.vipCache[playerName] ?: return true
            if (cache[vipName] == null) {
                sender.sendMessage(LangConfig.VipsRemoveNoVip)
                return false
            }

            cache.remove(vipName)
            VipManager.updateCargo(playerName)

            when (perm) {
                is Permission -> perm.playerRemoveGroup(VipManager.world, playerName, VipData.vipGroup[vipName])
                is net.milkbowl.vault2.permission.Permission -> perm.playerRemoveGroup(
                    VipManager.world,
                    playerName,
                    VipData.vipGroup[vipName]
                )
            }

            VipData.vipQuantity[vipName] = (VipData.vipQuantity[vipName] ?: 0) - 1

            PlayerData.discordCache[playerName]?.let { discordId ->
                VipData.vipDiscord[vipName]?.let { roleId ->
                    DiscordManager.removeUserRole(discordId, roleId)
                }
            }

            sender.sendMessage(LangConfig.VipsRemove)
            return false
        }

        // GENERATE VIP KEY
        if (subCommand == "gerarkey" && args.size == 3 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            val days = args[2].toLongOrNull() ?: return true

            if (VipData.vipExists(vipName)) {
                val key = KeyData.genNewVipKey(vipName, days)
                sender.sendMessage(LangConfig.VipsCreateNewKey.replace("%key%", key))
            }
            return false
        }

        // ASSIGN VIP to player with optional items
        if (subCommand == "dar" && args.size == 5 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            val playerName = args[1]
            val vipName = args[2]
            val days = args[3].toLongOrNull() ?: return true
            val giveItems = args[4].toBooleanStrictOrNull() ?: return true

            if (!VipData.vipExists(vipName)) {
                sender.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (!PlayerData.checkIfPlayerExists(playerName)) {
                PlayerData.createNewPlayerData(playerName.lowercase())
            }

            val existingItems = PlayerData.vipItems[playerName]?.size ?: 0
            val vipItemSize = VipData.vipItems[vipName]?.size ?: 0
            if ((90 - existingItems) <= vipItemSize) {
                sender.sendMessage(LangConfig.VipsClearItemsInventory)
                return false
            }

            val currentVipTime = PlayerData.vipCache[playerName]?.get(vipName) ?: 0L
            val millisVipTime = if (currentVipTime == 0L) System.currentTimeMillis() + days * 86_400_000 else currentVipTime + days * 86_400_000

            PlayerData.vipCache[playerName] = hashMapOf(vipName to millisVipTime)

            if (giveItems) PlayerData.vipItems[playerName] = VipData.vipItems[vipName]!!

            sender.sendMessage(LangConfig.VipsActivate.replace("%vip%", vipName).replace("%days%", days.toString()))

            PlayerUtil.sendAllMessage(
                LangConfig.VipsActivateMessage
                    .replace("%player%", playerName)
                    .replace(
                        "%time%",
                        TotalEssentials.getCore().getTime().convertMillisToString(days * 86_400_000, false)
                    )
                    .replace("%vip%", vipName)
            )

            VipManager.updateCargo(playerName, vipName, giveItems)

            DiscordManager.sendDiscordMessage(
                LangConfig.VipsDiscordActivateMessage
                    .replace("%player%", playerName)
                    .replace(
                        "%time%",
                        TotalEssentials.getCore().getTime().convertMillisToString(days * 86_400_000, false)
                    )
                    .replace("%vip%", vipName),
                true
            )

            return false
        }

        // ASSIGN Discord role to VIP
        if (subCommand == "discrole" && args.size == 3 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            val roleId = args[2].toLongOrNull() ?: return false

            if (!VipData.vipExists(vipName)) {
                sender.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            if (!DiscordManager.checkIfRoleIdExist(roleId)) {
                sender.sendMessage(LangConfig.VipsDiscordRoleError)
                return false
            }

            VipData.vipDiscord[vipName] = roleId
            sender.sendMessage(LangConfig.VipsDiscordRoleActivate)
            return false
        }

        // MANAGE VIP commands (add/remove/list)
        if (subCommand == "comando" && args.size >= 3 && sender.hasPermission("totalessentials.commands.vip.admin")) {
            val vipName = args[1]
            if (!VipData.vipExists(vipName)) {
                sender.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            when (args[2].lowercase()) {
                "add" -> {
                    val command = args.drop(3).joinToString(" ")
                    VipData.vipCommands[vipName] = arrayListOf(command)
                    sender.sendMessage(LangConfig.VipsCommandsAdd)
                }
                "remove" -> {
                    val command = args.drop(3).joinToString(" ")
                    VipData.vipCommands.remove(vipName, command)
                    sender.sendMessage(LangConfig.VipsCommandsRemove)
                }
                "list" -> {
                    sender.sendMessage(LangConfig.VipsCommandsListMessage)
                    VipData.vipCommands[vipName]?.forEach { cmd ->
                        sender.sendMessage(LangConfig.VipsCommandsList.replace("%command%", cmd))
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
        if (sender is Player && (subCommand == "itens" || subCommand == "items")) {
            val vipName = args.getOrNull(1)

            val inventory = if (vipName != null && sender.hasPermission("totalessentials.commands.vip.admin")) {
                if (!VipData.vipExists(vipName)) {
                    sender.sendMessage(LangConfig.VipsNotExist)
                    return false
                }
                val inv = TotalEssentials.getInstance().server.createInventory(null, 54, "§eVipEditItens $vipName")
                VipData.vipItems[vipName]?.forEach { inv.addItem(it) }
                Data.playerVipEdit[sender] = vipName
                inv
            } else {
                val playerItems = PlayerData.vipItems[sender] ?: emptyList()
                val size = if (playerItems.size > 54) 90 else 54
                val inv = TotalEssentials.getInstance().server.createInventory(null, size, "§eVipItens")
                playerItems.forEach { inv.addItem(it) }
                inv
            }

            sender.openInventory(inventory)
            return false
        }

        // VIEW VIP TIME
        if (subCommand == "tempo") {
            if (sender is Player && args.size == 1) {
                val cache = PlayerData.vipCache[sender]
                if (cache.isNullOrEmpty()) {
                    sender.sendMessage(LangConfig.VipsTimeNoVip)
                    return false
                }
                sender.sendMessage(LangConfig.VipsTimeFirstMessage)
                cache.forEach { (vip, time) ->
                    sender.sendMessage(
                        LangConfig.VipsTimeMessage.replace("%vipName%", vip).replace(
                            "%vipTime%",
                            TotalEssentials.getCore().getTime()
                                .convertMillisToString(time - System.currentTimeMillis(), false)
                        )
                    )
                }
            } else if (args.size == 2 && sender.hasPermission("totalessentials.commands.vip.admin")) {
                val targetPlayer = args[1]
                if (!PlayerData.checkIfPlayerExists(targetPlayer)) {
                    sender.sendMessage(LangConfig.generalPlayerNotExist)
                    return false
                }
                val cache = PlayerData.vipCache[targetPlayer]
                if (cache.isNullOrEmpty()) {
                    sender.sendMessage(LangConfig.VipsTimeNoVip)
                    return false
                }
                sender.sendMessage(LangConfig.VipsTimeFirstOtherMessage.replace("%player%", targetPlayer))
                cache.forEach { (vip, time) ->
                    sender.sendMessage(
                        LangConfig.VipsTimeMessage.replace("%vipName%", vip).replace(
                            "%vipTime%",
                            TotalEssentials.getCore().getTime()
                                .convertMillisToString(time - System.currentTimeMillis(), false)
                        )
                    )
                }
            }
            return false
        }

        // SWITCH VIP
        if (subCommand == "mudar" && sender is Player && args.size == 1) {
            val vipName = VipManager.updateCargo(sender.name.lowercase()) ?: return false
            sender.sendMessage(LangConfig.VipsSwitch.replace("%vipName%", vipName))
            return false
        }

        // DISCORD COMMANDS (linking and token usage)
        if (sender is Player) {
            when (subCommand) {
                "discord" -> {
                    val discordId = args.getOrNull(1)?.toLongOrNull() ?: return true
                    TotalEssentials.getCore().getTask().async {
                        val token = KeyData.generateRandomString()
                        if (DiscordManager.sendDiscordMessage(discordId, LangConfig.VipsDiscordMessage.replace("%value%", token))) {
                            Data.tokenVip[token] = discordId
                            sender.sendMessage(LangConfig.VipsDiscordLocalMessage)
                        } else sender.sendMessage(LangConfig.VipsDiscordUserIdNotExist)
                    }
                    return false
                }
                "token" -> {
                    val token = args.getOrNull(1) ?: return true
                    val discordId = Data.tokenVip[token] ?: run {
                        sender.sendMessage(LangConfig.VipsDiscordTokenError)
                        return false
                    }
                    sender.sendMessage(LangConfig.VipsDiscordTokenActivate)

                    // Remove previous roles
                    PlayerData.discordCache[sender]?.let { oldId ->
                        VipData.vipDiscord.getMap().forEach { (_, role) ->
                            DiscordManager.removeUserRole(oldId, role ?: return@forEach)
                        }
                    }

                    PlayerData.discordCache[sender] = discordId

                    // Remove all VIP roles and add new ones
                    VipData.vipDiscord.getMap().forEach { (_, role) ->
                        DiscordManager.removeUserRole(discordId, role ?: return@forEach)
                    }
                    PlayerData.vipCache[sender]?.forEach { (vip, _) ->
                        DiscordManager.addUserRole(discordId, VipData.vipDiscord[vip] ?: return@forEach)
                    }

                    Data.tokenVip.remove(token)
                    return false
                }
            }
        }

        // ADMIN ONLY: ADD TIME TO ALL VIPs
        if (subCommand == "timeadd" && sender !is Player && args.size == 2) {
            val addTime = args[1].toLongOrNull()?.times(86_400_000) ?: return true
            PlayerData.vipCache.getMap().forEach { (playerName, vipMap) ->
                vipMap?.forEach { (vipName, time) ->
                    PlayerData.vipCache[playerName]?.set(vipName, time + addTime)
                }
            }

            TotalEssentials.getCore().getTask().async {
                try {
                    transaction(totalCore?.sql) {
                        for (i in totalCore?.getCache()?.toByteUpdate!!) {
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
