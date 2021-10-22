package io.github.gilbertodamim.ksystem.commands.kits

import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.convertItems
import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.ksystem.config.configs.KitsConfig
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.management.Manager
import io.github.gilbertodamim.ksystem.management.dao.Dao
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
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
            if (args.size > 1) {
                val kit = args[0].lowercase()
                val kitcache = Dao.kitsCache[args[0].lowercase()] ?: run {
                    s.sendMessage(KitsLang.notExist)
                    return false
                }
                if (args[1] == "time") {
                    try {
                        editKit(kit, args[2], s)
                        s.sendMessage(
                            KitsLang.editKitSuccess.replace(
                                "%name%",
                                kit
                            )
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        s.sendMessage(
                            KitsLang.editKitProblem.replace(
                                "%name%",
                                kit
                            )
                        )
                    }
                    return false
                }
                if (args[1] == "items") {
                    editKitGui(kit, kitcache.items, s)
                    return false
                }
                if (args[1] == "name") {
                    try {
                        editKit(kit, args[2])
                        s.sendMessage(
                            KitsLang.editKitSuccess.replace(
                                "%name%",
                                kit
                            )
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        s.sendMessage(
                            KitsLang.editKitProblem.replace(
                                "%name%",
                                kit
                            )
                        )
                    }
                    return false
                }
                return false
            }
            return false
        }
        s.sendMessage(GeneralLang.notPerm)
        return false
    }

    private fun editKitGui(kit: String, items: Array<ItemStack?>, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(p, 36, kit)
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