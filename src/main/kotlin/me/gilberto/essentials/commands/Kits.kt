package me.gilberto.essentials.commands

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.config.configs.Kits.Useshorttime
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Kits.editkittime
import me.gilberto.essentials.database.PlayerKits
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.database.SqlKits
import me.gilberto.essentials.management.Dao.inventory
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.convertmilisstring
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
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
    private fun convertitens(to: String): ArrayList<ItemStack> {
        val itens = ArrayList<ItemStack>()
        for (i in to.split("^§^")) {
            val splited = i.split("_§_")
            val item = if (splited[2] == "-") {
                Material.getMaterial(splited[0])?.let { ItemStack(it, splited[1].toInt()) }
            }
            else {
                Material.getMaterial(splited[0])?.let { ItemStack(it, splited[1].toInt(), splited[2].toShort()) }
            }
            if (splited[3] != "-") {
                for (ii in splited[3].split("=§§=")) {
                    val splited2 = ii.split("=§=")
                    Enchantment.getByKey(NamespacedKey.fromString(splited2[0]))?.let { item?.addUnsafeEnchantment(it, splited2[1].toInt()) }
                }
            }
            val b = item?.itemMeta
            if (splited[4] != "-") {
                b?.setDisplayName(splited[4])
            }
            if (splited[5] != "-") {
                b?.lore = splited[5].split("=§=")
            }
            item?.itemMeta = b
            if (item != null) {
                itens.add(item)
            }
        }
        return itens
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
            val itemname = if(i.type.name == "AIR") continue else { i.type.name }
            val quant = check(i.amount.toString())
            var durability = i.durability.toString()
            if (durability == "0") {
                durability = "-"
            }
            val metadata = i.itemMeta
            var enchants = "-"
            var name = "-"
            var lore = "-"
            if (metadata != null) {
                name = if (metadata.hasDisplayName()) {
                    metadata.displayName
                } else "-"
                if (metadata.enchants.isNotEmpty()) {
                    for (toenchant in metadata.enchants) {
                        enchants = if (enchants == "-") {
                            "${toenchant.key.key}=§=${toenchant.value}"
                        } else {
                            "$enchants=§§=${toenchant.key.key}=§=${toenchant.value}"
                        }
                    }
                }
                if (metadata.lore != null && metadata.lore!!.isNotEmpty()) {
                    for (tolore in metadata.lore!!) {
                        lore = if (lore == "-") {
                            tolore
                        } else {
                            "$lore=§=$tolore"
                        }
                    }
                }
            }
            converted = if (converted == "") {
                "${itemname}_§_${quant}_§_${durability}_§_${enchants}_§_${name}_§_$lore"
            } else {
                "$converted^§^${itemname}_§_${quant}_§_${durability}_§_${enchants}_§_${name}_§_$lore"
            }
        }
        return converted
    }
    private fun createkitgui(kit: String, player: Player) {
        val inv = instance.server.createInventory(player, 36, kit)
        player.openInventory(inv)
        inventory[player] = kit
    }
    private fun createkit(kit: String, itens: Array<ItemStack>) {
        val ite = convertitens(itens)
        consoleMessage(ite)
        for(i in convertitens(ite)) {
            Bukkit.getPlayer("Gilberto")?.inventory?.addItem(i)
        }
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                SqlKits.insert {
                    it[kitname] = kit.lowercase()
                    it[kitrealname] = kit
                    it[kititens] = ite
                    it[kittime] = 0
                }
                PlayerKits.integer(kit.lowercase())
            }
        }, Executors.newCachedThreadPool())
    }
    private fun delkit(kit: String) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                SqlKits.deleteWhere { SqlKits.kitname like kit }
                val col = Column<Int>(SqlKits, kit.lowercase(), IntegerColumnType())
                val col1 = Column<Int>(PlayerKits, kit.lowercase(), IntegerColumnType())
                col.dropStatement()
                col1.dropStatement()
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
                SchemaUtils.create(PlayerKits)
            }
        }
    }
}