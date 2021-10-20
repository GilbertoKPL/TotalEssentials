package io.github.gilbertodamim.essentials.commands.kits

import io.github.gilbertodamim.essentials.config.langs.GeneralLang.onlyPlayerCommand
import io.github.gilbertodamim.essentials.database.SqlInstance
import io.github.gilbertodamim.essentials.database.table.PlayerKits
import io.github.gilbertodamim.essentials.database.table.SqlKits
import io.github.gilbertodamim.essentials.management.Dao.kitsItems
import io.github.gilbertodamim.essentials.management.Dao.kitsRealName
import io.github.gilbertodamim.essentials.management.Dao.kitsTime
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
            s.sendMessage(onlyPlayerCommand)
            return false
        }
        if (args.isNotEmpty() && s.hasPermission("gd.essentials.kits")) {
            if (kitsItems[args[0].lowercase()] != null) {
                for (i in kitsItems[args[0].lowercase()]!!) {
                    s.player?.inventory?.addItem(i)
                }
            }
            return false
        }
        return false
    }

    companion object {
        fun startKits() {
            try {
                transaction(SqlInstance.SQL) {
                    SchemaUtils.create(SqlKits, PlayerKits)
                }
            } finally {
                updateKits()
            }
        }

        fun updateKits() {
            CompletableFuture.runAsync({
                transaction(SqlInstance.SQL) {
                    for (items in SqlKits.selectAll()) {
                        val kit = items[SqlKits.kitName]
                        val kitRealName = items[SqlKits.kitRealName]
                        val kitTime = items[SqlKits.kitTime]
                        val item = items[SqlKits.kitItems]
                        kitsItems[kit] = convertItems(item)
                        kitsRealName[kit] = kitRealName
                        kitsTime[kit] = kitTime
                    }
                }
            }, Executors.newCachedThreadPool())
        }

        fun convertItems(to: Array<ItemStack>): String {
            fun check(to: String?): String {
                return to ?: "-"
            }

            var converted = ""
            for (item in to) {
                //support for lower versions.
                if (item == null) continue
                //support for lower versions.
                val itemName = if (item.type.name == "AIR") continue else {
                    item.type.name
                }
                val quantity = check(item.amount.toString())
                var durability = item.durability.toString()
                if (durability == "0") {
                    durability = "-"
                }
                val metadata = item.itemMeta
                var enchants = "-"
                var name = "-"
                var lore = "-"
                if (metadata != null) {
                    name = if (metadata.hasDisplayName()) {
                        metadata.displayName
                    } else "-"
                    if (metadata.enchants.isNotEmpty()) {
                        for (toEnchant in metadata.enchants) {
                            enchants = if (enchants == "-") {
                                "${toEnchant.key.name}=§=${toEnchant.value}"
                            } else {
                                "$enchants=§§=${toEnchant.key.name}=§=${toEnchant.value}"
                            }
                        }
                    }
                    if (metadata.lore != null && metadata.lore!!.isNotEmpty()) {
                        for (toLore in metadata.lore!!) {
                            lore = if (lore == "-") {
                                toLore
                            } else {
                                "$lore=§=$toLore"
                            }
                        }
                    }
                }
                converted = if (converted == "") {
                    "${itemName}_§_${quantity}_§_${durability}_§_${enchants}_§_${name}_§_$lore"
                } else {
                    "$converted^§^${itemName}_§_${quantity}_§_${durability}_§_${enchants}_§_${name}_§_$lore"
                }
            }
            return converted
        }

        fun convertItems(to: String): ArrayList<ItemStack> {
            val items = ArrayList<ItemStack>()
            for (i in to.split("^§^")) {
                val split = i.split("_§_")
                val item = if (split[2] == "-") {
                    Material.getMaterial(split[0])?.let { ItemStack(it, split[1].toInt()) }
                } else {
                    Material.getMaterial(split[0])?.let { ItemStack(it, split[1].toInt(), split[2].toShort()) }
                }
                if (split[3] != "-") {
                    for (ii in split[3].split("=§§=")) {
                        val split2 = ii.split("=§=")
                        Enchantment.getByName(split2[0])?.let { item?.addUnsafeEnchantment(it, split2[1].toInt()) }
                    }
                }
                val b = item?.itemMeta
                if (split[4] != "-") {
                    b?.setDisplayName(split[4])
                }
                if (split[5] != "-") {
                    b?.lore = split[5].split("=§=")
                }
                item?.itemMeta = b
                if (item != null) {
                    items.add(item)
                }
            }
            return items
        }
    }
}