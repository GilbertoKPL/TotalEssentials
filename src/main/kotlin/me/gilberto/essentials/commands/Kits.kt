package me.gilberto.essentials.commands

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.config.configs.Kits.Useshorttime
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Kits.editkittime
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.database.SqlKits
import me.gilberto.essentials.management.Dao.inventory
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.convertmilisstring
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class Kits : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        createkitgui("asd", s as Player)
        return false
    }

    fun event(e: InventoryCloseEvent) : Boolean {
        if (inventory.contains(e.player)) {
            createkit(inventory[e.player]!!, e.inventory.contents)
            inventory.remove(e.player)
            return true
        }
        return false
    }

    private fun convertitens(to: Array<ItemStack>): String {
        fun check(to: String?): String {
            return to ?: "-"
        }
        var converted = ""
        for (i in to) {
            //support for lower versions.
            if (i == null) continue
            //support for lower versions.
            val id = if(i.type.id == 0) continue else {
                i.type.id.toString()
            }
            val data = if (i.data != null) {
                i.data?.data.toString()
            } else {
                "0"
            }
            val quant = check(i.amount.toString())
            var durability = i.durability.toString()
            if (durability == "0") {
                durability = "-"
            }
            val metadata = i.itemMeta
            var enchants = "-"
            var name = "-"
            if (metadata != null) {
                name = if (metadata.hasDisplayName()) {
                    metadata.displayName
                } else "-"
                for (toenchant in metadata.enchants) {
                    enchants = if (enchants == "-") {
                        "${toenchant.key}=§=${toenchant.value}"
                    } else {
                        "$enchants=§§=${toenchant.value}"
                    }
                }
            }
            converted = if (converted == "") {
                "${id}_§_${data}_§_${quant}_§_${durability}_§_${enchants}_§_$name"
            } else {
                "$converted-§-${id}_§_${data}_§_${quant}_§_${durability}_§_${enchants}_§_$name"
            }
            consoleMessage(converted)
        }
        return converted
    }
    private fun createkitgui(kit: String, player: Player) {
        val inv = instance.server.createInventory(player, 27, kit)
        player.openInventory(inv)
        inventory[player] = kit
    }
    private fun createkit(kit: String, itens: Array<ItemStack>) {
        val ite = convertitens(itens)
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                SqlKits.insert {
                    it[kitname] = kit.lowercase()
                    it[kitrealname] = kit
                    it[kititens] = ite
                    it[kittime] = 0
                }
            }
        }, Executors.newCachedThreadPool())
    }

    fun editkit(kit: String, namekit: String, s: CommandSender) {
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
        }, Executors.newSingleThreadExecutor())
    }

    fun editkit(kit: String, time: Int, unit: String, s: CommandSender) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                val work = SqlKits.select { SqlKits.kitname eq kit.lowercase() }
                if (work.empty()) {
                    s.sendMessage(Kits.notexist)
                    return@transaction
                }
                val convert = when (unit.lowercase()) {
                    "s" -> TimeUnit.SECONDS.toMillis(time.toLong())
                    "m" -> TimeUnit.MINUTES.toMillis(time.toLong())
                    "h" -> TimeUnit.HOURS.toMillis(time.toLong())
                    "d" -> TimeUnit.DAYS.toMillis(time.toLong())
                    else -> TimeUnit.MINUTES.toMillis(time.toLong())
                }
                s.sendMessage(editkittime.replace("%time%", convertmilisstring(convert, Useshorttime)))
                SqlKits.update({ SqlKits.kitname eq kit.lowercase() }) {
                    it[kittime] = convert
                }
            }
        }, Executors.newCachedThreadPool())
    }

    companion object {
        fun startkits() {
            transaction(SqlInstance.SQL) {
                SchemaUtils.create(SqlKits)
            }
        }
    }
}