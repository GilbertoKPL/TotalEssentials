package me.gilberto.essentials.commands

import me.gilberto.essentials.config.configs.Kits.Useshorttime
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Kits.editkittime
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.management.Manager.convertmilisstring
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object SqlKits : Table() {
    val kitname = varchar("kitname", 16)
    val kitrealname = varchar("kitrealname", 32)
    val kittime = long("kittime").default(0)
    val kititens = text("kititens").default("")

    override val primaryKey = PrimaryKey(kitname)
}

@Suppress("DEPRECATION")
class Kits : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        return false
    }

    private fun convertitens(to: List<ItemStack>): String {
        var converted = ""
        fun check(to: String?): String {
            return to ?: "_"
        }
        for (i in to) {
            val id = i.type.id
            val data = i.data?.data
            val quant = check(i.amount.toString())
            val metadata = i.itemMeta!!
            val durability = i.durability
            var enchants = "_"
            val name = if (metadata.hasDisplayName()) {
                metadata.displayName
            } else "_"
            for (toenchant in metadata.enchants) {
                enchants = if (enchants == "_") {
                    toenchant.toString()
                } else {
                    "$enchants%$toenchant"
                }
            }
            converted = "$converted-$id-$data.$quant.$durability.$enchants.$name"
        }
        return converted
    }

    fun createkit(kit: String, itens: List<ItemStack>) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                SqlKits.insert {
                    it[kitname] = kit.lowercase()
                    it[kitrealname] = kit
                    it[kititens] = convertitens(itens)
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
        }, Executors.newCachedThreadPool())
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