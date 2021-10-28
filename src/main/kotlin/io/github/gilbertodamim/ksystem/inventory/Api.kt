package io.github.gilbertodamim.ksystem.inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object Api {
    fun item(material: Material, name: String, lore: List<String>, effect: Boolean = false): ItemStack {
        val item = ItemStack(material)
        if (effect) {
            item.addUnsafeEnchantment(Enchantment.LUCK, 1)
        }
        val meta = item.itemMeta
        meta?.lore = lore
        meta?.setDisplayName(name)
        if (effect) {
            meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        item.itemMeta = meta
        return item
    }

    fun item(material: Material, name: String, effect: Boolean = false): ItemStack {
        val item = ItemStack(material)
        if (effect) {
            item.addUnsafeEnchantment(Enchantment.LUCK, 1)
        }
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        if (effect) {
            meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        item.itemMeta = meta
        return item
    }

    fun item(material: Material, name: String, quant: Int): ItemStack {
        val item = ItemStack(material, quant)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        item.itemMeta = meta
        return item
    }

    fun item(material: Material, quant: Int): ItemStack {
        return ItemStack(material, quant)
    }

    fun item(material: Material): ItemStack {
        return ItemStack(material)
    }
}