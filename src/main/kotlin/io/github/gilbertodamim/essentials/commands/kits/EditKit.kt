package io.github.gilbertodamim.essentials.commands.kits

import io.github.gilbertodamim.essentials.EssentialsMain
import io.github.gilbertodamim.essentials.commands.kits.Kit.Companion.convertItems
import io.github.gilbertodamim.essentials.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.essentials.config.configs.KitsConfig
import io.github.gilbertodamim.essentials.config.langs.GeneralLang
import io.github.gilbertodamim.essentials.database.SqlInstance
import io.github.gilbertodamim.essentials.database.table.SqlKits
import io.github.gilbertodamim.essentials.management.Dao
import io.github.gilbertodamim.essentials.management.Manager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.select
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
        if (s.hasPermission("gd.essentials.kits.admin")) {
            if (args.size > 1) {
                val kit = args[0].lowercase()
                if (Dao.kitsItems[args[0].lowercase()] != null) {
                    if (args[1] == "time" && args.size > 2) {
                        try {
                            editKit(kit, args[2].toInt(), args[3], s)
                            s.sendMessage(
                                io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitSuccess.replace(
                                    "%name%",
                                    kit
                                )
                            )
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            s.sendMessage(
                                io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitProblem.replace(
                                    "%name%",
                                    kit
                                )
                            )
                        }
                        return false
                    }
                    if (args[1] == "items") {
                        editKitGui(kit, s)
                        return false
                    }
                    if (args[1] == "name") {
                        try {
                            editKit(kit, args[2], s)
                            s.sendMessage(
                                io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitSuccess.replace(
                                    "%name%",
                                    kit
                                )
                            )
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            s.sendMessage(
                                io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitProblem.replace(
                                    "%name%",
                                    kit
                                )
                            )
                        }
                        return false
                    }
                    return false
                }
                s.sendMessage(io.github.gilbertodamim.essentials.config.langs.KitsLang.notExist)
                return false
            }
            return false
        }
        s.sendMessage(GeneralLang.notPerm)
        return false
    }

    private fun editKitGui(kit: String, p: Player) {
        val inv = EssentialsMain.instance.server.createInventory(p, 36, kit)
        for (items in Dao.kitsItems[kit]!!) {
            inv.addItem(items)
        }
        p.openInventory(inv)
        Dao.editKitInventory[p] = kit
    }

    private fun editKit(kit: String, nameKit: String, s: CommandSender) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                val work = SqlKits.select { SqlKits.kitName eq kit.lowercase() }
                if (work.empty()) {
                    s.sendMessage(io.github.gilbertodamim.essentials.config.langs.KitsLang.notExist)
                    return@transaction
                }
                SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                    it[kitRealName] = nameKit
                }
            }
            updateKits()
        }, Executors.newSingleThreadExecutor())
    }

    private fun editKit(kit: String, time: Int, unit: String?, s: CommandSender) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                val work = SqlKits.select { SqlKits.kitName eq kit.lowercase() }
                if (work.empty()) {
                    s.sendMessage(io.github.gilbertodamim.essentials.config.langs.KitsLang.notExist)
                    return@transaction
                }
                val convert = if (unit == null) {
                    TimeUnit.MINUTES.toMillis(time.toLong())
                } else {
                    when (unit.lowercase()) {
                        "s" -> TimeUnit.SECONDS.toMillis(time.toLong())
                        "m" -> TimeUnit.MINUTES.toMillis(time.toLong())
                        "h" -> TimeUnit.HOURS.toMillis(time.toLong())
                        "d" -> TimeUnit.DAYS.toMillis(time.toLong())
                        else -> TimeUnit.MINUTES.toMillis(time.toLong())
                    }
                }
                s.sendMessage(
                    io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitTime.replace(
                        "%time%",
                        Manager.convertMillisToString(convert, KitsConfig.useShortTime)
                    )
                )
                SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                    it[kitTime] = convert
                }
                updateKits()
            }
        }, Executors.newCachedThreadPool())
    }

    private fun editKit(kit: String, items: Array<ItemStack>) {
        CompletableFuture.runAsync({
            val ite = convertItems(items)
            transaction(SqlInstance.SQL) {
                SqlKits.update({ SqlKits.kitName eq kit.lowercase() }) {
                    it[kitItems] = ite
                }
            }
            updateKits()
        }, Executors.newCachedThreadPool())
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.editKitInventory.contains(e.player)) {
            try {
                editKit(Dao.editKitInventory[e.player]!!, e.inventory.contents)
                e.player.sendMessage(
                    io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitSuccess.replace(
                        "%name%",
                        Dao.editKitInventory[e.player]!!
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
                e.player.sendMessage(
                    io.github.gilbertodamim.essentials.config.langs.KitsLang.editKitProblem.replace(
                        "%name%",
                        Dao.editKitInventory[e.player]!!
                    )
                )
            }
            Dao.editKitInventory.remove(e.player)
            return true
        }
        return false
    }
}