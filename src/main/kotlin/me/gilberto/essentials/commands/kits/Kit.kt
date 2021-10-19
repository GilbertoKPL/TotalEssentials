package me.gilberto.essentials.commands.kits

import me.gilberto.essentials.config.configs.langs.General.onlyplayercommand
import me.gilberto.essentials.database.PlayerKits
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.database.SqlKits
import me.gilberto.essentials.management.Dao.kitsitens
import me.gilberto.essentials.management.Dao.kitsrealname
import me.gilberto.essentials.management.Dao.kitstime
import me.gilberto.essentials.management.Manager.consoleMessage
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class Kit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(onlyplayercommand)
            return false
        }
        if (args.isNotEmpty() && s.hasPermission("gd.essentials.kits")) {
            if (kitsitens[args[0].lowercase()] != null) {
                for (i in kitsitens[args[0].lowercase()]!!) {
                    s.player?.inventory?.addItem(i)
                }
            }
            return false
        }
        return false
    }

    companion object {
        fun startkits() {
            try {
                transaction(SqlInstance.SQL) {
                    SchemaUtils.create(SqlKits, PlayerKits)
                }
            }
            finally {
                updatekits()
            }
        }

        fun updatekits() {
            CompletableFuture.runAsync({
                transaction(SqlInstance.SQL) {
                    for (i in SqlKits.selectAll()) {
                        val kit = i[SqlKits.kitname]
                        val kitrealname = i[SqlKits.kitrealname]
                        val kittime = i[SqlKits.kittime]
                        val ite = i[SqlKits.kititens]
                        consoleMessage(ite)
                        kitsitens[kit] = convertitens(ite)
                        kitsrealname[kit] = kitrealname
                        kitstime[kit] = kittime
                    }
                }
            }, Executors.newCachedThreadPool())
        }

        fun convertitens(to: Array<ItemStack>): String {
            fun check(to: String?): String {
                return to ?: "-"
            }

            var converted = ""
            for (i in to) {
                //support for lower versions.
                if (i == null) continue
                //support for lower versions.
                val itemname = if (i.type.name == "AIR") continue else {
                    i.type.name
                }
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
                                "${toenchant.key.name}=§=${toenchant.value}"
                            } else {
                                "$enchants=§§=${toenchant.key.name}=§=${toenchant.value}"
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

        fun convertitens(to: String): ArrayList<ItemStack> {
            val itens = ArrayList<ItemStack>()
            for (i in to.split("^§^")) {
                val splited = i.split("_§_")
                val item = if (splited[2] == "-") {
                    Material.getMaterial(splited[0])?.let { ItemStack(it, splited[1].toInt()) }
                } else {
                    Material.getMaterial(splited[0])?.let { ItemStack(it, splited[1].toInt(), splited[2].toShort()) }
                }
                if (splited[3] != "-") {
                    for (ii in splited[3].split("=§§=")) {
                        val splited2 = ii.split("=§=")
                        Enchantment.getByName(splited2[0])?.let { item?.addUnsafeEnchantment(it, splited2[1].toInt()) }
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
    }
}