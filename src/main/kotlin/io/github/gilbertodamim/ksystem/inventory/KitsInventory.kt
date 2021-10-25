package io.github.gilbertodamim.ksystem.inventory

import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryItemsLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryItemsName
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryNameLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryNameName
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryTimeLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryTimeName
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitInventoryItemsLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitInventoryItemsName
import io.github.gilbertodamim.ksystem.inventory.Api.item
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.Dao.EditKitGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitClickGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitGuiCache

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class KitsInventory {
    fun editKitInventory() {
        for (inventory in 0..26) {
            if (inventory == 11) {
                EditKitGuiCache.put(inventory, item(Material.CHEST, editkitInventoryItemsName, editkitInventoryItemsLore))
                continue
            }
            if (inventory == 13) {
                EditKitGuiCache.put(inventory, item(Material.CLOCK, editkitInventoryTimeName, editkitInventoryTimeLore))
                continue
            }
            if (inventory == 15) {
                EditKitGuiCache.put(inventory, item(Material.NAME_TAG, editkitInventoryNameName, editkitInventoryNameLore))
                continue
            }
            EditKitGuiCache.put(inventory, item(Material.AIR))
        }
    }
    fun kitGuiInventory() {
        var size = 1
        var length = 0
        var inv = KSystemMain.instance.server.createInventory(null, 36, "Kits 1")
        for (i in Dao.kitsCache.asMap()) {
            var item = ItemStack(Material.CHEST)
            val name = kitInventoryItemsName.replace("%kitrealname%", i.value.get().realName)
            for (to in i.value.get().items) {
                if (to != null) {
                    item = ItemStack(to.type)
                    break
                }
            }
            val meta = item.itemMeta
            item.amount = 1
            meta?.setDisplayName(name)
            val itemLore  = ArrayList<String>()
            for (lore in kitInventoryItemsLore) {
                itemLore.add(lore.replace("%name%", i.key))
            }
            meta?.lore = itemLore
            item.itemMeta = meta
            val cacheValue = (length + 1) + ((size - 1) * 27)
            kitClickGuiCache.put(cacheValue, i.key)
            if (length < 26) {
                inv.setItem(length, item)
                length += 1
            }
            else {
                inv.setItem(length, item)
                if (size > 1) {
                    inv.setItem(27, item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
                }
                inv.setItem(35, item(Material.ARROW, KitsLang.kitInventoryIconNextName))
                kitGuiCache.put(size, inv)
                length = 0
                size += 1
                inv = KSystemMain.instance.server.createInventory(null, 36, "Kits $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(27, item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
            }
            kitGuiCache.put(size, inv)
        }
    }
}