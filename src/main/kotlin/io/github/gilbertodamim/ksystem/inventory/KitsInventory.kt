package io.github.gilbertodamim.ksystem.inventory

import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryItemsLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryItemsName
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryNameLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryNameName
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryTimeLore
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.editkitInventoryTimeName
import io.github.gilbertodamim.ksystem.inventory.Api.item
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.Dao.EditKitGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitGuiCache

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class KitsInventory {
    fun editKitInventory() {
        for (inventory in 0..26) {
            if (inventory == 11) {
                EditKitGuiCache.put(inventory, item(Material.CHEST, editkitInventoryItemsName, editkitInventoryItemsLore.split("/")))
                continue
            }
            if (inventory == 13) {
                EditKitGuiCache.put(inventory, item(Material.CLOCK, editkitInventoryTimeName, editkitInventoryTimeLore.split("/")))
                continue
            }
            if (inventory == 15) {
                EditKitGuiCache.put(inventory, item(Material.NAME_TAG, editkitInventoryNameName, editkitInventoryNameLore.split("/")))
                continue
            }
            EditKitGuiCache.put(inventory, item(Material.AIR))
        }
    }
    fun kitGuiInventory() {
        var size = 1
        var length = 0
        var inv = KSystemMain.instance.server.createInventory(null, 36, "Kits-1")
        for (i in Dao.kitsCache.asMap()) {
            var item = ItemStack(Material.AIR)
            for (to in i.value.get().items) {
                if (to != null) {
                    item = to
                    break
                }
            }
            val meta = item.itemMeta
            meta?.setDisplayName(i.key)
            item.itemMeta = meta
            if (length < 27) {
                inv.setItem(length, item)
            }
            else {
                if (size > 1) {
                    inv.setItem(27, item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
                }
                inv.setItem(35, item(Material.ARROW, KitsLang.kitInventoryIconNextName))
                kitGuiCache.put(size, inv)
                length = 0
                size += 1
                inv = KSystemMain.instance.server.createInventory(null, 36, "Kits-$size")
            }
            length += 1
        }
        if (length > 0) {
            inv.setItem(27, item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
            kitGuiCache.put(size, inv)
        }
    }
}