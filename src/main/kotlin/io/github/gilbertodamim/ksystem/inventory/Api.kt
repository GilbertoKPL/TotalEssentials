package io.github.gilbertodamim.ksystem.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Api {
    fun item(material: Material, name: String, lore: List<String>): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.lore = lore
        meta?.setDisplayName(name)
        item.itemMeta = meta
        return item
    }

    fun item(material: Material, name: String): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
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