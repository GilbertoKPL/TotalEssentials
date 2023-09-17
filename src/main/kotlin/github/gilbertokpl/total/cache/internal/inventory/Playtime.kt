package github.gilbertokpl.total.cache.internal.inventory

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

object Playtime {
    private val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§ePLAYTIME", true)

    fun setup() {
        val inventoryCache: MutableMap<Int, Inventory> = linkedMapOf()
        var size = 1
        var length = 0
        var inv = TotalEssentials.instance.server.createInventory(null, 36, "§ePLAYTIME 1")

        val cache = PlayerData.playTimeCache.getMap().toList().sortedByDescending { (_, value) -> value }

        for ((player, time) in cache.take(135)) {

            val item = createHeadItem(player, time ?: 0L)

            if (length < 26) {
                inv.setItem(length, item)
                length++
            } else {
                inv.setItem(length, item)
                for (to in 27..35) {
                    when {
                        to == 27 && size > 1 -> inv.setItem(
                            to,
                            ItemUtil.item(
                                Material.HOPPER,
                                LangConfig.playtimeInventoryIconBackName,
                                true
                            )
                        )
                        to == 35 -> inv.setItem(
                            to,
                            ItemUtil.item(
                                Material.ARROW,
                                LangConfig.playtimeInventoryIconNextName,
                                true
                            )
                        )
                        else -> inv.setItem(to, GLASS_MATERIAL)
                    }
                }
                inventoryCache[size] = inv
                length = 0
                size++
                inv = TotalEssentials.instance.server.createInventory(null, 36, "§ePLAYTIME $size")
            }
        }

        if (length > 0) {
            inv.setItem(
                27,
                if (size != 1) {
                    ItemUtil.item(
                        Material.HOPPER,
                        LangConfig.playtimeInventoryIconBackName
                    )
                } else {
                    GLASS_MATERIAL
                }
            )

            for (to in 28..35) {
                inv.setItem(to, GLASS_MATERIAL)
            }

            inventoryCache[size] = inv
        }

        Data.playTimeInventoryCache = Collections.unmodifiableMap(inventoryCache)
    }

    fun createHeadItem(name: String, time: Long): ItemStack {
        val playerName = LangConfig.playtimeInventoryItemsName.replace("%player%", name)
        val playtimeInventoryItemsLore = LangConfig.playtimeInventoryItemsLore
        val item = ItemStack(MaterialUtil["head"]!!, 1, SkullType.PLAYER.ordinal.toShort())
        val meta = item.itemMeta

        meta?.setDisplayName(playerName)

        val itemLore = ArrayList<String>(playtimeInventoryItemsLore.size)

        val t1 = PlayerData.playtimeLocal[name] ?: 0L
        val t = time + if (t1 != 0L) System.currentTimeMillis() - t1 else 0L

        playtimeInventoryItemsLore.forEach {
            itemLore.add(it.replace("%time%", TotalEssentials.basePlugin.getTime().convertMillisToString(t, true)))
        }

        if (meta != null) {
            meta.lore = itemLore
        }

        item.itemMeta = meta

        return item
    }
}
