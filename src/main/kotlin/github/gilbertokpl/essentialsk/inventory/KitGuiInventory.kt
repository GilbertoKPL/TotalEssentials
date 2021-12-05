package github.gilbertokpl.essentialsk.inventory

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object KitGuiInventory {
    fun kitGuiInventory() {
        Dao.getInstance().kitClickGuiCache.invalidateAll()
        var size = 1
        var length = 0
        var inv = EssentialsK.instance.server.createInventory(null, 36, "§eKits 1")
        for (i in Dao.getInstance().kitsCache.asMap()) {
            var item = ItemStack(Material.CHEST)
            val name = GeneralLang.getInstance().kitsInventoryItemsName.replace("%kitrealname%", i.value.get().fakeName)
            for (to in i.value.get().items) {
                item = ItemStack(to.type)
                break
            }
            val meta = item.itemMeta
            item.amount = 1
            meta?.setDisplayName(name)


            val itemLore = ArrayList<String>()
            GeneralLang.getInstance().kitsInventoryItemsLore.forEach {
                itemLore.add(it.replace("%name%", i.key))
            }

            meta?.lore = itemLore
            item.itemMeta = meta
            val cacheValue = (length + 1) + ((size - 1) * 27)
            Dao.getInstance().kitClickGuiCache.put(cacheValue, i.key)

            if (length < 26) {
                inv.setItem(length, item)
                length += 1
            } else {
                inv.setItem(length, item)
                for (to in 27..35) {
                    if (to == 27) {
                        if (size > 1) {
                            inv.setItem(
                                to,
                                ItemUtil.getInstance()
                                    .item(Material.HOPPER, GeneralLang.getInstance().kitsInventoryIconBackName, true)
                            )
                            continue
                        }
                    }
                    if (to == 35) {
                        inv.setItem(
                            to,
                            ItemUtil.getInstance()
                                .item(Material.ARROW, GeneralLang.getInstance().kitsInventoryIconNextName, true)
                        )
                        continue
                    }
                    inv.setItem(
                        to,
                        ItemUtil.getInstance().item(Dao.getInstance().material["glass"]!!, "§eKIT", true)
                    )
                }
                Dao.getInstance().kitGuiCache[size] = inv
                length = 0
                size += 1
                inv = EssentialsK.instance.server.createInventory(null, 36, "§eKits $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(
                    27,
                    ItemUtil.getInstance().item(Material.HOPPER, GeneralLang.getInstance().kitsInventoryIconBackName)
                )
            } else {
                inv.setItem(27, ItemUtil.getInstance().item(Dao.getInstance().material["glass"]!!, "§eKIT", true))
            }
            for (to in 28..35) {
                inv.setItem(
                    to,
                    ItemUtil.getInstance().item(Dao.getInstance().material["glass"]!!, "§eKIT", true)
                )
            }
            Dao.getInstance().kitGuiCache[size] = inv
        }
    }
}