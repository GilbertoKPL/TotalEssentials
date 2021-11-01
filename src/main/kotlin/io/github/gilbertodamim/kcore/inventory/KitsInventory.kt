package io.github.gilbertodamim.kcore.inventory

import io.github.gilbertodamim.kcore.KCoreMain
import io.github.gilbertodamim.kcore.KCoreMain.pluginName
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editkitInventoryItemsLore
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editkitInventoryItemsName
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editkitInventoryNameLore
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editkitInventoryNameName
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editkitInventoryTimeLore
import io.github.gilbertodamim.kcore.config.langs.KitsLang.editkitInventoryTimeName
import io.github.gilbertodamim.kcore.config.langs.KitsLang.kitInventoryItemsLore
import io.github.gilbertodamim.kcore.config.langs.KitsLang.kitInventoryItemsName
import io.github.gilbertodamim.kcore.inventory.Api.item
import io.github.gilbertodamim.kcore.management.Manager.consoleMessage
import io.github.gilbertodamim.kcore.management.dao.Dao
import io.github.gilbertodamim.kcore.management.dao.Dao.EditKitGuiCache
import io.github.gilbertodamim.kcore.management.dao.Dao.kitClickGuiCache
import io.github.gilbertodamim.kcore.management.dao.Dao.kitGuiCache

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class KitsInventory {
    fun editKitInventory() {
        EditKitGuiCache.invalidateAll()
        for (inventory in 0..26) {
            if (inventory == 11) {
                EditKitGuiCache.put(
                    inventory,
                    item(Material.CHEST, editkitInventoryItemsName, editkitInventoryItemsLore)
                )
                continue
            }
            if (inventory == 13) {
                EditKitGuiCache.put(inventory,
                    item(Dao.Materials["clock"]!!, editkitInventoryTimeName, editkitInventoryTimeLore)
                )
                continue
            }
            if (inventory == 15) {
                    EditKitGuiCache.put(
                        inventory,
                        item(Dao.Materials["feather"]!!, editkitInventoryNameName, editkitInventoryNameLore)
                    )
                continue
            }
            EditKitGuiCache.put(inventory,
                item(Dao.Materials["glass"]!!, "${pluginName}§eKIT", true)
            )
        }
    }

    fun kitGuiInventory() {
        var size = 1
        var length = 0
        var inv = KCoreMain.instance.server.createInventory(null, 36, "$pluginName§eKits 1")
        kitGuiCache.invalidateAll()
        kitClickGuiCache.invalidateAll()
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
            val itemLore = ArrayList<String>()
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
            } else {
                inv.setItem(length, item)
                for (to in 27..35) {
                    if (to == 27) {
                        if (size > 1) {
                            inv.setItem(to, item(Material.HOPPER, KitsLang.kitInventoryIconBackName, true))
                            continue
                        }
                    }
                    if (to == 35) {
                        inv.setItem(to, item(Material.ARROW, KitsLang.kitInventoryIconNextName, true))
                        continue
                    }
                    inv.setItem(to,
                        item(Dao.Materials["glass"]!!, "${pluginName}§eKIT", true)
                    )
                }
                kitGuiCache.put(size, inv)
                length = 0
                size += 1
                inv = KCoreMain.instance.server.createInventory(null, 36, "$pluginName§eKits $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(27, item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
            }
            else {
                inv.setItem(27, item(Dao.Materials["glass"]!!, "${pluginName}§eKIT", true))
            }
            for (to in 28..35) {
                inv.setItem(to,
                    item(Dao.Materials["glass"]!!, "${pluginName}§eKIT", true)
                )
            }
            kitGuiCache.put(size, inv)
        }
    }
}