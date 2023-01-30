package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.ItemStack

object PlaytimeInventory {
    private val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§ePLAYTIME", true)

    fun setup() {
        DataManager.playTimeGuiCache.clear()
        var size = 1
        var length = 0
        var inv = TotalEssentials.instance.server.createInventory(null, 36, "§ePLAYTIME 1")

        val cache = PlayerData.playTimeCache.getMap().toList().sortedBy { (_, value) -> value }.reversed().toMap()

        for (time in cache) {
            val name = LangConfig.playtimeInventoryItemsName.replace(
                "%player%",
                time.key
            )
            val item = ItemStack(MaterialUtil["head"]!!, 1, SkullType.PLAYER.ordinal.toShort())
            val meta = item.itemMeta

            item.amount = 1
            meta?.setDisplayName(name)

            val itemLore = ArrayList<String>()

            val t1 = (PlayerData.playtimeLocal[time.key] ?: 0L)

            val t = time.value!! + if (t1 != 0L) System.currentTimeMillis() - t1 else 0L

            LangConfig.playtimeInventoryItemsLore.forEach {
                itemLore.add(it.replace("%time%", TotalEssentials.basePlugin.getTime().convertMillisToString(t, true)))
            }

            meta?.lore = itemLore
            item.itemMeta = meta

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
                                    LangConfig.playtimeInventoryIconBackName,
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
                                    LangConfig.playtimeInventoryIconNextName,
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
                DataManager.playTimeGuiCache[size] = inv
                length = 0
                size += 1
                inv = TotalEssentials.instance.server.createInventory(null, 36, "§ePLAYTIME $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(
                    27,
                    ItemUtil.item(
                        Material.HOPPER,
                        LangConfig.playtimeInventoryIconBackName
                    )
                )
            } else {
                inv.setItem(
                    27,
                    GLASS_MATERIAL
                )
            }
            for (to in 28..35) {
                inv.setItem(
                    to,
                    GLASS_MATERIAL
                )
            }
            DataManager.playTimeGuiCache[size] = inv
        }
    }
}