package me.gilberto.essentials.commands.kits

import me.gilberto.essentials.EssentialsMain
import me.gilberto.essentials.commands.kits.Kit.Companion.convertitens
import me.gilberto.essentials.commands.kits.Kit.Companion.updatekits
import me.gilberto.essentials.config.configs.langs.General
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.database.SqlKits
import me.gilberto.essentials.management.Dao
import me.gilberto.essentials.management.Manager
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
            s.sendMessage(General.onlyplayercommand)
            return false
        }
        if (s.hasPermission("gd.essentials.kits.admin")) {
            if (args.size > 1) {
                val kit = args[0].lowercase()
                if (Dao.kitsitens[args[0].lowercase()] != null){
                    if (args[1] == "time" && args.size > 2) {
                        try {
                            editkit(kit, args[2].toInt(), args[3], s)
                            s.sendMessage(Kits.editkitsuccess.replace("%name%", kit))
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            s.sendMessage(Kits.editkitproblem.replace("%name%", kit))
                        }
                        return false
                    }
                    if (args[1] == "itens") {
                        editkitgui(kit, s)
                        return false
                    }
                    if (args[1] == "name") {
                        try {
                            editkit(kit, args[2], s)
                            s.sendMessage(Kits.editkitsuccess.replace("%name%", kit))
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            s.sendMessage(Kits.editkitproblem.replace("%name%", kit))
                        }
                        return false
                    }
                    return false
                }
                s.sendMessage(Kits.notexist)
                return false
            }
            return false
        }
        s.sendMessage(General.notperm)
        return false
    }

    private fun editkitgui(kit: String, p: Player) {
        val inv = EssentialsMain.instance.server.createInventory(p, 36, kit)
        for (i in Dao.kitsitens[kit]!!) {
            inv.addItem(i)
        }
        p.openInventory(inv)
        Dao.editinventory[p] = kit
    }

    private fun editkit(kit: String, namekit: String, s: CommandSender) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                val work = SqlKits.select { SqlKits.kitname eq kit.lowercase() }
                if (work.empty()) {
                    s.sendMessage(Kits.notexist)
                    return@transaction
                }
                SqlKits.update({ SqlKits.kitname eq kit.lowercase() }) {
                    it[kitrealname] = namekit
                }
            }
            updatekits()
        }, Executors.newSingleThreadExecutor())
    }

    private fun editkit(kit: String, time: Int, unit: String?, s: CommandSender) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                val work = SqlKits.select { SqlKits.kitname eq kit.lowercase() }
                if (work.empty()) {
                    s.sendMessage(Kits.notexist)
                    return@transaction
                }
                val convert = if (unit == null) { TimeUnit.MINUTES.toMillis(time.toLong()) }
                else {
                    when (unit.lowercase()) {
                        "s" -> TimeUnit.SECONDS.toMillis(time.toLong())
                        "m" -> TimeUnit.MINUTES.toMillis(time.toLong())
                        "h" -> TimeUnit.HOURS.toMillis(time.toLong())
                        "d" -> TimeUnit.DAYS.toMillis(time.toLong())
                        else -> TimeUnit.MINUTES.toMillis(time.toLong())
                    }
                }
                s.sendMessage(
                    Kits.editkittime.replace(
                        "%time%",
                        Manager.convertmilisstring(convert, me.gilberto.essentials.config.configs.Kits.Useshorttime)
                    )
                )
                SqlKits.update({ SqlKits.kitname eq kit.lowercase() }) {
                    it[kittime] = convert
                }
                updatekits()
            }
        }, Executors.newCachedThreadPool())
    }

    private fun editkit(kit: String, itens: Array<ItemStack>) {
        CompletableFuture.runAsync({
            val ite = convertitens(itens)
            transaction(SqlInstance.SQL) {
                SqlKits.update({ SqlKits.kitname eq kit.lowercase() }) {
                    it[kititens] = ite
                }
            }
            updatekits()
        }, Executors.newCachedThreadPool())
    }

    fun event(e: InventoryCloseEvent): Boolean {
        if (Dao.editinventory.contains(e.player)) {
            try {
                editkit(Dao.editinventory[e.player]!!, e.inventory.contents)
                e.player.sendMessage(Kits.editkitsuccess.replace("%name%", Dao.editinventory[e.player]!!))
            } catch (ex: Exception) {
                ex.printStackTrace()
                e.player.sendMessage(Kits.editkitproblem.replace("%name%", Dao.editinventory[e.player]!!))
            }
            Dao.editinventory.remove(e.player)
            return true
        }
        return false
    }
}