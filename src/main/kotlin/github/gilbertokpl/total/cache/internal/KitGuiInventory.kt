package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.HashUtil
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

internal object KitGuiInventory {

    private val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§eKIT", true)

    fun setup() {
        DataManager.ClickKitGuiCache.clear()
        DataManager.kitGuiCache.clear()
        var size = 1
        var length = 0
        var inv = github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 36, "§eKits 1")

        val newHash = LinkedHashMap<String, Int>()

        KitsData.kitWeight.getMap().forEach {
            val value = it.value
            if (value != null) {
                newHash[it.key] = value
            }
        }

        val cache = HashUtil.hashMapReverse(HashUtil.hashMapSortMap(newHash))


        for (kit in cache) {
            val name = LangConfig.kitsInventoryItemsName.replace(
                "%kitrealname%",
                KitsData.kitFakeName[kit.key].let {
                    if (it == null || it == "") kit.key else it
                }
            )
            val item = try {
                ItemStack(KitsData.kitItems[kit.key]!![0])
            } catch (e: Throwable) {
                ItemStack(Material.CHEST)
            }
            val meta = item.itemMeta
            item.amount = 1
            meta?.setDisplayName(name)


            val itemLore = ArrayList<String>()
            LangConfig.kitsInventoryItemsLore.forEach {
                itemLore.add(it.replace("%realname%", kit.key))
            }

            meta?.lore = itemLore
            item.itemMeta = meta
            val cacheValue = (length + 1) + ((size - 1) * 27)
            DataManager.ClickKitGuiCache[cacheValue] = kit.key

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
                                    LangConfig.kitsInventoryIconBackName,
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
                                    LangConfig.kitsInventoryIconNextName,
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
                DataManager.kitGuiCache[size] = inv
                length = 0
                size += 1
                inv = github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 36, "§eKits $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(
                    27,
                    ItemUtil.item(
                        Material.HOPPER,
                        LangConfig.kitsInventoryIconBackName
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
            DataManager.kitGuiCache[size] = inv
        }
    }

    fun kitGui(kit: String, guiNumber: String, p: Player) {
        //create gui
        val inv =
            github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 45, "§eKit $kit $guiNumber")

        //get all time of kits
        var timeAll = PlayerData.kitsCache[p]?.get(kit) ?: 0L

        timeAll += KitsData.kitTime[kit] ?: 0L

        //all items
        for (items in KitsData.kitItems[kit]!!) {
            inv.addItem(items)
        }


        for (to1 in 36..44) {
            if (to1 == 36) {
                inv.setItem(
                    to1,
                    ItemUtil
                        .item(
                            Material.HOPPER,
                            LangConfig.kitsInventoryIconBackName,
                            true
                        )
                )
                continue
            }
            if (to1 == 40 && p.hasPermission("totalessentials.commands.editkit")) {
                inv.setItem(
                    to1,
                    ItemUtil
                        .item(
                            Material.CHEST,
                            LangConfig.kitsInventoryIconEditKitName,
                            true
                        )
                )
                continue
            }
            if (to1 == 44) {
                if (p.hasPermission("totalessentials.commands.kit.$kit")) {
                    if (timeAll <= System.currentTimeMillis() ||
                        timeAll == 0L ||
                        p.hasPermission("totalessentials.bypass.kitcatch")
                    ) {
                        inv.setItem(
                            to1,
                            ItemUtil.item(
                                Material.ARROW,
                                LangConfig.kitsGetIcon,
                                true
                            )
                        )
                        continue
                    }
                    val array = ArrayList<String>()
                    val remainingTime = timeAll - System.currentTimeMillis()
                    for (i in LangConfig.kitsGetIconLoreTime) {
                        array.add(
                            i.replace(
                                "%time%",
                                github.gilbertokpl.total.TotalEssentials.basePlugin.getTime()
                                    .convertMillisToString(
                                        remainingTime,
                                        MainConfig.kitsUseShortTime
                                    )
                            )
                        )
                    }
                    inv.setItem(
                        to1,
                        ItemUtil
                            .item(
                                Material.ARROW,
                                LangConfig.kitsGetIconNotCatch,
                                array,
                                true
                            )
                    )
                    continue
                }
                inv.setItem(
                    to1,
                    ItemUtil.item(
                        Material.ARROW,
                        LangConfig.kitsGetIconNotCatch,
                        LangConfig.kitsGetIconLoreNotPerm,
                        true
                    )
                )
                continue
            }
            inv.setItem(
                to1,
                GLASS_MATERIAL
            )
        }
        p.openInventory(inv)
    }
}
