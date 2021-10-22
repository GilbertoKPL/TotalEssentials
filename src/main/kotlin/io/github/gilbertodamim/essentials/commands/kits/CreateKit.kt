package io.github.gilbertodamim.essentials.commands.kits

import io.github.gilbertodamim.essentials.EssentialsMain
import io.github.gilbertodamim.essentials.commands.kits.Kit.Companion.convertItems
import io.github.gilbertodamim.essentials.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.essentials.config.langs.GeneralLang
import io.github.gilbertodamim.essentials.config.langs.KitsLang
import io.github.gilbertodamim.essentials.config.langs.KitsLang.Exist
import io.github.gilbertodamim.essentials.config.langs.KitsLang.createKitUsage
import io.github.gilbertodamim.essentials.database.SqlInstance
import io.github.gilbertodamim.essentials.database.table.PlayerKits
import io.github.gilbertodamim.essentials.database.table.SqlKits
import io.github.gilbertodamim.essentials.management.dao.Dao
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
        if (s.hasPermission("gd.essentials.kits.admin")) {
            if (args.size == 1) {
                if (Dao.kitsCache[args[0]] == null) {
                    createKitGui(args[0], s)
                }
                else {
                    s.sendMessage(Exist)
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
        val inv = EssentialsMain.instance.server.createInventory(p, 36, kit)
        p.openInventory(inv)
        Dao.kitInventory[p] = kit
    }

    private fun createKit(kit: String, items: Array<ItemStack?>) {
        CompletableFuture.runAsync({
            try {
                val item = convertItems(items)
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
                updateKits()
            }
            catch (e : Exception) {
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