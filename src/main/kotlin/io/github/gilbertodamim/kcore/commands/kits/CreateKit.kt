package io.github.gilbertodamim.kcore.commands.kits

import io.github.gilbertodamim.kcore.KCoreMain
import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang.Exist
import io.github.gilbertodamim.kcore.config.langs.KitsLang.createKitUsage
import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.dao.Dao
import io.github.gilbertodamim.kcore.dao.KCoreKit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
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
        if (s.hasPermission("kcore.kits.admin")) {
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
        val inv = KCoreMain.instance.server.createInventory(p, 36, kit)
        p.openInventory(inv)
        Dao.kitInventory[p] = kit
    }

    private fun createKit(kit: String, items: Array<ItemStack?>) {
        val item = Kit().convertItems(items)
        Dao.kitsCache.put(
            kit,
            KCoreKit(
                kit.lowercase(),
                0,
                kit,
                Kit().convertItems(item)
            )
        )
        KitsInventory().kitGuiInventory()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.insert {
                        it[kitName] = kit.lowercase()
                        it[kitRealName] = kit
                        it[kitItems] = item
                        it[kitTime] = 0L
                    }
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
            }
        }, Executors.newCachedThreadPool())
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.kitInventory.contains(e.player as Player)) {
            val p = e.player as Player
            try {
                createKit(Dao.kitInventory[p]!!, e.inventory.contents)
                p.sendMessage(KitsLang.createKitSuccess.replace("%name%", Dao.kitInventory[p]!!))
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
                p.sendMessage(KitsLang.createKitProblem.replace("%name%", Dao.kitInventory[p]!!))
            }
            Dao.kitInventory.remove(p)
            return true
        }
        return false
    }
}