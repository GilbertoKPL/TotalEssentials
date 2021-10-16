package me.gilberto.essentials.commands.kits

import me.gilberto.essentials.EssentialsMain
import me.gilberto.essentials.commands.kits.Kit.Companion.checkkitexist
import me.gilberto.essentials.commands.kits.Kit.Companion.convertitens
import me.gilberto.essentials.commands.kits.Kit.Companion.updatekits
import me.gilberto.essentials.config.configs.langs.General
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Kits.createkitusage
import me.gilberto.essentials.config.configs.langs.Kits.exist
import me.gilberto.essentials.database.PlayerKits
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.database.SqlKits
import me.gilberto.essentials.management.Dao
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
            s.sendMessage(General.onlyplayercommand)
            return false
        }
        if (s.hasPermission("gd.essentials.kits.admin")) {
            if (args.size == 1) {
                createkitgui(args[0], s)
            } else {
                s.sendMessage(createkitusage)
            }
            return false
        }
        s.sendMessage(General.notperm)
        return false
    }

    private fun createkitgui(kit: String, p: Player) {
        if (checkkitexist(kit.lowercase()).get()) {
            p.sendMessage(exist)
            return
        }
        val inv = EssentialsMain.instance.server.createInventory(p, 36, kit)
        p.openInventory(inv)
        Dao.inventory[p] = kit
    }

    private fun createkit(kit: String, itens: Array<ItemStack>) {
        CompletableFuture.runAsync({
            val ite = convertitens(itens)
            transaction(SqlInstance.SQL) {
                SqlKits.insert {
                    it[kitname] = kit.lowercase()
                    it[kitrealname] = kit
                    it[kititens] = ite
                    it[kittime] = 0
                }
                PlayerKits.integer(kit.lowercase())
                SchemaUtils.createMissingTablesAndColumns(PlayerKits)
            }
        }, Executors.newCachedThreadPool())
        updatekits()
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.inventory.contains(e.player)) {
            try {
                createkit(Dao.inventory[e.player]!!, e.inventory.contents)
                e.player.sendMessage(Kits.createkitsuccess.replace("%name%", Dao.inventory[e.player]!!))
            } catch (ex: Exception) {
                ex.printStackTrace()
                e.player.sendMessage(Kits.createkitproblem.replace("%name%", Dao.inventory[e.player]!!))
            }
            Dao.inventory.remove(e.player)
            return true
        }
        return false
    }
}