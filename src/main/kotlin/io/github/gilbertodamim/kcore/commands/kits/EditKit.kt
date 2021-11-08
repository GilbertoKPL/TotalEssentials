package io.github.gilbertodamim.kcore.commands.kits

import io.github.gilbertodamim.kcore.KCoreMain
import io.github.gilbertodamim.kcore.commands.kits.Kit.Companion.convertItems
import io.github.gilbertodamim.kcore.config.configs.KitsConfig
import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editKitInventoryNameMessage
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editKitInventoryTimeMessage
import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.management.Manager
import io.github.gilbertodamim.kcore.management.dao.Dao
import io.github.gilbertodamim.kcore.management.dao.Dao.ChatEventKit
import io.github.gilbertodamim.kcore.management.dao.Dao.EditKitGuiCache
import io.github.gilbertodamim.kcore.management.dao.Dao.kitsCache
import io.github.gilbertodamim.kcore.management.dao.KCoreKit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EditKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(GeneralLang.onlyPlayerCommand)
            return false
        }
        if (s.hasPermission("kcore.kits.admin")) {
            if (args.isNotEmpty()) {
                val kit = args[0].lowercase()
                kitsCache.getIfPresent(kit) ?: run {
                    s.sendMessage(KitsLang.notExist)
                    return false
                }
                editKitGui(kit, s)
            }
            return false
        }
        s.sendMessage(GeneralLang.notPerm)
        return false
    }

    fun editKitMessageEvent(e: AsyncPlayerChatEvent): Boolean {
        val toCheck = ChatEventKit[e.player] ?: return false
        ChatEventKit.remove(e.player)
        e.isCancelled = true
        val split = toCheck.split("-")
        val s = e.player
        if (split[0] == "time") {
            try {
                editKit(split[1], e.message, s)
                s.sendMessage(KitsLang.editKitSuccess.replace("%name%", split[1]))
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
                s.sendMessage(KitsLang.editKitProblem.replace("%name%", split[1]))
            }
        }
        if (split[0] == "name") {
            try {
                if (e.message.length > 16) {
                    s.sendMessage(KitsLang.nameLength)
                } else {
                    editKit(split[1], e.message.replace("&", "§"))
                    s.sendMessage(
                        KitsLang.editKitSuccess.replace("%name%", split[1])
                    )
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
                s.sendMessage(KitsLang.editKitProblem.replace("%name%", split[1]))
            }
        }
        return true
    }

    fun editKitGuiEvent(e: InventoryClickEvent): Boolean {
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == ("${KCoreMain.pluginName}§eEditKit") && e.currentItem != null) {
            e.isCancelled = true
            val number = e.slot
            val p = e.whoClicked as Player
            if (number == 11) {
                p.closeInventory()
                kitsCache.getIfPresent(inventoryName[1])?.items?.let {
                    editKitGui(
                        inventoryName[1],
                        it,
                        p
                    )
                }
                return true
            }
            if (number == 13) {
                p.closeInventory()
                p.sendMessage(editKitInventoryTimeMessage)
                ChatEventKit[p] = "time-${inventoryName[1]}"
                return true
            }
            if (number == 15) {
                p.closeInventory()
                p.sendMessage(editKitInventoryNameMessage)
                ChatEventKit[p] = "name-${inventoryName[1]}"
                return true
            }
            return true
        }
        return false
    }

    fun editKitGui(kit: String, p: Player) {
        val inv = KCoreMain.instance.server.createInventory(null, 27, "${KCoreMain.pluginName}§eEditKit $kit")
        for (i in EditKitGuiCache.asMap()) {
            inv.setItem(i.key, i.value)
        }
        p.openInventory(inv)
    }

    private fun editKitGui(kit: String, items: Array<ItemStack?>, p: Player) {
        val inv = KCoreMain.instance.server.createInventory(null, 36, kit)
        for (i in items) {
            inv.addItem(i ?: continue)
        }
        p.openInventory(inv)
        Dao.kitEditInventory[p] = kit
    }

    private fun editKit(kit: String, nameKit: String) {
        val editValues = kitsCache.getIfPresent(kit)
        if (editValues != null) {
            kitsCache.invalidate(kit)
            kitsCache.put(
                kit,

                KCoreKit(
                    editValues.name,
                    editValues.time,
                    nameKit,
                    editValues.items
                )
            )
        }
        KitsInventory().kitGuiInventory()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                        it[kitRealName] = nameKit
                    }
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun editKit(kit: String, time: String, s: CommandSender) {
        val messageSplit = time.split(" ")
        var convert = 0L
        for (i in messageSplit) {
            val split = i.replace("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$".toRegex(), "1")
                .split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex())
            val unit = try {
                split[1]
            } catch (e: Exception) {
                null
            }
            convert += if (unit == null) {
                try {
                    TimeUnit.MINUTES.toMillis(split[0].toLong())
                } catch (e: Exception) {
                    0L
                }
            } else {
                when (unit.lowercase()) {
                    "s" -> TimeUnit.SECONDS.toMillis(split[0].toLong())
                    "m" -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                    "h" -> TimeUnit.HOURS.toMillis(split[0].toLong())
                    "d" -> TimeUnit.DAYS.toMillis(split[0].toLong())
                    else -> try {
                        TimeUnit.MINUTES.toMillis(split[0].toLong())
                    } catch (e: Exception) {
                        0L
                    }
                }
            }
        }
        val editValues = kitsCache.getIfPresent(kit)
        if (editValues != null) {
            kitsCache.invalidate(kit)
            kitsCache.put(
                kit,
                KCoreKit(
                    editValues.name,
                    convert,
                    editValues.realName,
                    editValues.items
                )
            )
        }
        KitsInventory().kitGuiInventory()
        s.sendMessage(
            KitsLang.editKitTime.replace(
                "%time%",
                Manager.convertMillisToString(convert, KitsConfig.useShortTime)
            )
        )
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                        it[kitTime] = convert
                    }
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
            }
        }, Executors.newCachedThreadPool())
    }

    private fun editKit(kit: String, items: Array<ItemStack?>) {
        val editValues = kitsCache.getIfPresent(kit)
        if (editValues != null) {
            kitsCache.invalidate(kit)
            kitsCache.put(
                kit,

                KCoreKit(
                    editValues.name,
                    editValues.time,
                    editValues.realName,
                    items
                )
            )
        }
        KitsInventory().kitGuiInventory()
        CompletableFuture.runAsync({
            try {
                val ite = convertItems(items)
                transaction(SqlInstance.SQL) {
                    SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                        it[kitItems] = ite
                    }
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
            }
        }, Executors.newCachedThreadPool())
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.kitEditInventory.contains(e.player as Player)) {
            val p = e.player as Player
            try {
                editKit(Dao.kitEditInventory[p]!!, e.inventory.contents)
                p.sendMessage(
                    KitsLang.editKitSuccess.replace(
                        "%name%",
                        Dao.kitEditInventory[p]!!
                    )
                )
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
                p.sendMessage(
                    KitsLang.editKitProblem.replace(
                        "%name%",
                        Dao.kitEditInventory[p]!!
                    )
                )
            }
            Dao.kitEditInventory.remove(p)
            return true
        }
        return false
    }
}