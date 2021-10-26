package io.github.gilbertodamim.ksystem.commands.kits

import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.convertItems
import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.Exist
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.createKitUsage
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.PlayerKits
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.KSystemKit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


class CreateKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(GeneralLang.onlyPlayerCommand)
            return false
        }
        if (s.hasPermission("ksystem.kits.admin")) {
            if (args.size == 1) {
                if (args[0].length < 17) {
                    if (Dao.kitsCache.getIfPresent(args[0]) == null) {
                        createKitGui(args[0], s)
                    } else {
                        s.sendMessage(Exist)
                    }
                } else {
                    s.sendMessage(KitsLang.nameLength)
                }
            } else {
                s.sendMessage(createKitUsage)
            }
            return false
        }
        s.sendMessage(GeneralLang.notPerm)
        return false
    }

    private fun createKitGui(kit: String, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(p, 36, kit)
        p.openInventory(inv)
        Dao.kitInventory[p] = kit
    }

    private fun createKit(kit: String, items: Array<ItemStack?>) {
        val item = convertItems(items)
        Dao.kitsCache.put(
            kit,
            CompletableFuture.supplyAsync {
                KSystemKit(
                    kit.lowercase(),
                    0,
                    kit,
                    convertItems(item)
                )
            })
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.insert {
                        it[kitName] = kit.lowercase()
                        it[kitRealName] = kit
                        it[kitItems] = item
                        it[kitTime] = 0
                    }
                    PlayerKits.long(kit.lowercase())
                    SchemaUtils.createMissingTablesAndColumns(PlayerKits)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newCachedThreadPool())
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.kitInventory.contains(e.player)) {
            try {
                createKit(Dao.kitInventory[e.player]!!, e.inventory.contents)
                e.player.sendMessage(KitsLang.createKitSuccess.replace("%name%", Dao.kitInventory[e.player]!!))
            } catch (ex: Exception) {
                ex.printStackTrace()
                e.player.sendMessage(KitsLang.createKitProblem.replace("%name%", Dao.kitInventory[e.player]!!))
            }
            Dao.kitInventory.remove(e.player)
            return true
        }
        return false
    }
}