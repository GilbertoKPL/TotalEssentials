package github.gilbertokpl.essentialsk.inventory

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.util.HashUtil
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.MaterialUtil
import github.gilbertokpl.essentialsk.util.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

internal object KitGuiInventory {

    private val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§eKIT", true)

    fun setup() {
        DataManager.kitClickGuiCache.clear()
        DataManager.kitGuiCache.clear()
        var size = 1
        var length = 0
        var inv = EssentialsK.instance.server.createInventory(null, 36, "§eKits 1")

        val newHash = LinkedHashMap<String, Int>()

        KitData.getMap().forEach {
            newHash[it.key] = it.value.weightCache
        }

        val cache = HashUtil.hashMapReverse(HashUtil.hashMapSortMap(newHash))


        for (kit in cache) {
            val i = KitData[kit.key]!!
            val name = LangConfig.kitsInventoryItemsName.replace("%kitrealname%", i.fakeNameCache)
            val item = try {
                ItemStack(i.itemsCache[0])
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
            DataManager.kitClickGuiCache[cacheValue] = kit.key

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
                                .item(Material.HOPPER, LangConfig.kitsInventoryIconBackName, true)
                        )
                        continue
                    }
                    if (to == 35) {
                        inv.setItem(
                            to,
                            ItemUtil
                                .item(Material.ARROW, LangConfig.kitsInventoryIconNextName, true)
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
                inv = EssentialsK.instance.server.createInventory(null, 36, "§eKits $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(
                    27,
                    ItemUtil.item(Material.HOPPER, LangConfig.kitsInventoryIconBackName)
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
        val inv = EssentialsK.instance.server.createInventory(null, 45, "§eKit $kit $guiNumber")

        //load caches
        val kitCache = KitData[kit] ?: return
        val playerCache = PlayerData[p]!!

        //get all time of kits
        var timeAll = playerCache.kitsCache[kit] ?: 0L

        timeAll += kitCache.timeCache

        //all items
        for (items in kitCache.itemsCache) {
            inv.addItem(items)
        }


        for (to1 in 36..44) {
            if (to1 == 36) {
                inv.setItem(
                    to1,
                    ItemUtil
                        .item(Material.HOPPER, LangConfig.kitsInventoryIconBackName, true)
                )
                continue
            }
            if (to1 == 40 && p.hasPermission("essentialsk.commands.editkit")) {
                inv.setItem(
                    to1,
                    ItemUtil
                        .item(Material.CHEST, LangConfig.kitsInventoryIconEditKitName, true)
                )
                continue
            }
            if (to1 == 44) {
                if (p.hasPermission("essentialsk.commands.kit.$kit")) {
                    if (timeAll <= System.currentTimeMillis() ||
                        timeAll == 0L ||
                        p.hasPermission("essentialsk.bypass.kitcatch")
                    ) {
                        inv.setItem(
                            to1,
                            ItemUtil.item(Material.ARROW, LangConfig.kitsCatchIcon, true)
                        )
                        continue
                    }
                    val array = ArrayList<String>()
                    val remainingTime = timeAll - System.currentTimeMillis()
                    for (i in LangConfig.kitsCatchIconLoreTime) {
                        array.add(
                            i.replace(
                                "%time%",
                                TimeUtil
                                    .convertMillisToString(remainingTime, MainConfig.kitsUseShortTime)
                            )
                        )
                    }
                    inv.setItem(
                        to1,
                        ItemUtil
                            .item(Material.ARROW, LangConfig.kitsCatchIconNotCatch, array, true)
                    )
                    continue
                }
                inv.setItem(
                    to1,
                    ItemUtil.item(
                        Material.ARROW,
                        LangConfig.kitsCatchIconNotCatch,
                        LangConfig.kitsCatchIconLoreNotPerm,
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
