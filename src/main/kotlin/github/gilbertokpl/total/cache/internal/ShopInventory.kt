package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.cache.local.ShopData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.ItemStack

object ShopInventory {
    val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§eSHOP", true)

    fun setup() {
        DataManager.shopItemCache.clear()
        DataManager.shopInventoryCache.clear()
        var size = 1
        var length = 0
        var inv = github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 36, "§eSHOP 1")

        val cache = ShopData.shopVisits.getMap().toList().sortedBy { (_, value) -> value }.reversed().toMap()

        for (shop in cache) {
            val name = LangConfig.shopInventoryItemsName.replace(
                "%player%",
                shop.key
            )
            val item = ItemStack(MaterialUtil["head"]!!, 1, SkullType.PLAYER.ordinal.toShort())
            val meta = item.itemMeta

            item.amount = 1
            meta?.setDisplayName(name)

            val checkIfIsOpen = if (ShopData.shopOpen[shop.key]!!) {
                LangConfig.shopOpen
            } else {
                LangConfig.shopClosed
            }

            val itemLore = ArrayList<String>()
            LangConfig.shopInventoryItemsLore.forEach {
                itemLore.add(it.replace("%visits%", shop.value.toString()).replace("%open%", checkIfIsOpen))
            }

            meta?.lore = itemLore
            item.itemMeta = meta
            val cacheValue = (length + 1) + ((size - 1) * 27)
            DataManager.shopItemCache[cacheValue] = shop.key

            if (length < 26) {
                inv.setItem(length, item)
                length += 1
            } else {
                inv.setItem(length, item)
                for (to in 27..35) {
                    if (to == 27 && size > 1) {
                        inv.setItem(
                            to,
                            ItemUtil
                                .item(
                                    Material.HOPPER,
                                    LangConfig.shopInventoryIconBackName,
                                    true
                                )
                        )
                        continue
                    }
                    if (to == 35) {
                        inv.setItem(
                            to,
                            ItemUtil
                                .item(
                                    Material.ARROW,
                                    LangConfig.shopInventoryIconNextName,
                                    true
                                )
                        )
                        continue
                    }
                    inv.setItem(
                        to,
                        GLASS_MATERIAL
                    )
                }
                DataManager.shopInventoryCache[size] = inv
                length = 0
                size += 1
                inv = github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 36, "§eSHOP $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(
                    27,
                    ItemUtil.item(
                        Material.HOPPER,
                        LangConfig.shopInventoryIconBackName
                    )
                )
            } else {
                inv.setItem(
                    27,
                    GLASS_MATERIAL
                )
            }
            for (to in 28..35) {
                if (to == 30 || to == 32) continue
                inv.setItem(
                    to,
                    GLASS_MATERIAL
                )
            }
            DataManager.shopInventoryCache[size] = inv
        }
    }
}