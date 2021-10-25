package io.github.gilbertodamim.ksystem.commands.kits

import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.convertItems
import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.ksystem.config.configs.KitsConfig
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryNameMessage
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryTimeMessage
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.management.Manager
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.Dao.ChatEventKit
import io.github.gilbertodamim.ksystem.management.dao.Dao.EditKitGuiCache
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
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
        if (s.hasPermission("ksystem.kits.admin")) {
            if (args.isNotEmpty()) {
                val kit = args[0].lowercase()
                Dao.kitsCache.getIfPresent(kit) ?: run {
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

    fun editKitMessageEvent(e: AsyncPlayerChatEvent) : Boolean {
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
                ex.printStackTrace()
                s.sendMessage(KitsLang.editKitProblem.replace("%name%", split[1]))
            }
        }
        if (split[0] == "name") {
            try {
                if (e.message.length > 16) {
                    s.sendMessage(KitsLang.nameLength)
                }
                else {
                    editKit(split[1], e.message.replace("&", "§"))
                    s.sendMessage(
                        KitsLang.editKitSuccess.replace("%name%", split[1])
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                s.sendMessage(KitsLang.editKitProblem.replace("%name%", split[1]))
            }
        }
        return true
    }

    fun editKitGuiEvent(e: InventoryClickEvent) : Boolean {
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == ("EditKit") && e.currentItem != null) {
            e.isCancelled = true
            val number = e.slot
            val p = e.whoClicked
            if (number == 11) {
                p.closeInventory()
                Dao.kitsCache.getIfPresent(inventoryName[1])?.get()?.items?.let { editKitGui(inventoryName[1], it, p as Player) }
                return true
            }
            if (number == 13) {
                p.closeInventory()
                p.sendMessage(editkitInventoryTimeMessage)
                ChatEventKit[p as Player] = "time-${inventoryName[1]}"
                return true
            }
            if (number == 15) {
                p.closeInventory()
                p.sendMessage(editkitInventoryNameMessage)
                ChatEventKit[p as Player] = "name-${inventoryName[1]}"
                return true
            }
            return true
        }
        return false
    }

    private fun editKitGui(kit: String, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(null, 27, "EditKit $kit")
        for (i in EditKitGuiCache.asMap()) {
            inv.setItem(i.key, i.value)
        }
        p.openInventory(inv)
    }

    private fun editKitGui(kit: String, items: Array<ItemStack?>, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(null, 36, kit)
        for (i in items) {
            inv.addItem(i ?: continue)
        }
        p.openInventory(inv)
        Dao.kitEditInventory[p] = kit
    }

    private fun editKit(kit: String, nameKit: String) {
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                        it[kitRealName] = nameKit
                    }
                }
                updateKits()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun editKit(kit: String, time: String, s: CommandSender) {
        CompletableFuture.runAsync({
            try {
                val split = time.replace("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$".toRegex(), "1")
                    .split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex())
                val unit = try {
                    split[1]
                } catch (e: Exception) {
                    null
                }
                val convert = if (unit == null) {
                    TimeUnit.MINUTES.toMillis(split[0].toLong())
                } else {
                    when (unit.lowercase()) {
                        "s" -> TimeUnit.SECONDS.toMillis(split[0].toLong())
                        "m" -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                        "h" -> TimeUnit.HOURS.toMillis(split[0].toLong())
                        "d" -> TimeUnit.DAYS.toMillis(split[0].toLong())
                        else -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                    }
                }
                s.sendMessage(
                    KitsLang.editKitTime.replace(
                        "%time%",
                        Manager.convertMillisToString(convert, KitsConfig.useShortTime)
                    )
                )
                transaction(SqlInstance.SQL) {
                    SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                        it[kitTime] = convert
                    }
                    updateKits()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newCachedThreadPool())
    }

    private fun editKit(kit: String, items: Array<ItemStack?>) {
        CompletableFuture.runAsync({
            try {
                val ite = convertItems(items)
                transaction(SqlInstance.SQL) {
                    SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                        it[kitItems] = ite
                    }
                }
                updateKits()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newCachedThreadPool())
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.kitEditInventory.contains(e.player)) {
            try {
                editKit(Dao.kitEditInventory[e.player]!!, e.inventory.contents)
                e.player.sendMessage(
                    KitsLang.editKitSuccess.replace(
                        "%name%",
                        Dao.kitEditInventory[e.player]!!
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
                e.player.sendMessage(
                    KitsLang.editKitProblem.replace(
                        "%name%",
                        Dao.kitEditInventory[e.player]!!
                    )
                )
            }
            Dao.kitEditInventory.remove(e.player)
            return true
        }
        return false
    }
}