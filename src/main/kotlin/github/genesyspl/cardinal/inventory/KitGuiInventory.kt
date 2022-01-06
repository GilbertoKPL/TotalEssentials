package github.genesyspl.cardinal.inventory

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.util.HashUtil
import github.genesyspl.cardinal.util.ItemUtil
import github.genesyspl.cardinal.util.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object KitGuiInventory {
    fun setup() {
        DataManager.getInstance().kitClickGuiCache.clear()
        DataManager.getInstance().kitGuiCache.clear()
        var size = 1
        var length = 0
        var inv = github.genesyspl.cardinal.Cardinal.instance.server.createInventory(null, 36, "§eKits 1")

        val newHash = LinkedHashMap<String, Int>()

        DataManager.getInstance().kitCacheV2.forEach {
            newHash[it.key] = it.value.weight
        }


        val cache = HashUtil.getInstance().hashMapReverse(HashUtil.getInstance().hashMapSortMap(newHash))


        for (kit in cache) {
            val i = DataManager.getInstance().kitCacheV2[kit.key]!!
            var item = ItemStack(Material.CHEST)
            val name = github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryItemsName.replace(
                "%kitrealname%",
                i.fakeName
            )
            for (to in i.items) {
                item = ItemStack(to)
                break
            }
            val meta = item.itemMeta
            item.amount = 1
            meta?.setDisplayName(name)


            val itemLore = ArrayList<String>()
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryItemsLore.forEach {
                itemLore.add(it.replace("%realname%", kit.key))
            }

            meta?.lore = itemLore
            item.itemMeta = meta
            val cacheValue = (length + 1) + ((size - 1) * 27)
            DataManager.getInstance().kitClickGuiCache[cacheValue] = kit.key

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
                                    .item(
                                        Material.HOPPER,
                                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryIconBackName,
                                        true
                                    )
                            )
                            continue
                        }
                    }
                    if (to == 35) {
                        inv.setItem(
                            to,
                            ItemUtil.getInstance()
                                .item(
                                    Material.ARROW,
                                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryIconNextName,
                                    true
                                )
                        )
                        continue
                    }
                    inv.setItem(
                        to,
                        ItemUtil.getInstance().item(DataManager.getInstance().material["glass"]!!, "§eKIT", true)
                    )
                }
                DataManager.getInstance().kitGuiCache[size] = inv
                length = 0
                size += 1
                inv = github.genesyspl.cardinal.Cardinal.instance.server.createInventory(null, 36, "§eKits $size")
            }
        }
        if (length > 0) {
            if (size != 1) {
                inv.setItem(
                    27,
                    ItemUtil.getInstance().item(
                        Material.HOPPER,
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryIconBackName
                    )
                )
            } else {
                inv.setItem(
                    27,
                    ItemUtil.getInstance().item(DataManager.getInstance().material["glass"]!!, "§eKIT", true)
                )
            }
            for (to in 28..35) {
                inv.setItem(
                    to,
                    ItemUtil.getInstance().item(DataManager.getInstance().material["glass"]!!, "§eKIT", true)
                )
            }
            DataManager.getInstance().kitGuiCache[size] = inv
        }
    }

    fun kitGui(kit: String, guiNumber: String, p: Player) {
        //create gui
        val inv = github.genesyspl.cardinal.Cardinal.instance.server.createInventory(null, 45, "§eKit $kit $guiNumber")

        //load caches
        val kitCache = DataManager.getInstance().kitCacheV2[kit] ?: return
        val playerCache = DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!

        //get all time of kits
        var timeAll = playerCache.kitsCache[kit] ?: 0L

        timeAll += kitCache.time

        //all items
        for (items in kitCache.items) {
            inv.addItem(items)
        }


        for (to1 in 36..44) {
            if (to1 == 36) {
                inv.setItem(
                    to1,
                    ItemUtil.getInstance()
                        .item(
                            Material.HOPPER,
                            github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryIconBackName,
                            true
                        )
                )
                continue
            }
            if (to1 == 40) {
                if (p.hasPermission("cardinal.commands.editkit")) {
                    inv.setItem(
                        to1,
                        ItemUtil.getInstance()
                            .item(
                                Material.CHEST,
                                github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryIconEditKitName,
                                true
                            )
                    )
                    continue
                }
            }
            if (to1 == 44) {
                if (p.hasPermission("cardinal.commands.kit.$kit")) {
                    if (timeAll <= System.currentTimeMillis() || timeAll == 0L || p.hasPermission("cardinal.bypass.kitcatch")) {
                        inv.setItem(
                            to1,
                            ItemUtil.getInstance().item(
                                Material.ARROW,
                                github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIcon,
                                true
                            )
                        )
                        continue
                    }
                    val array = ArrayList<String>()
                    val remainingTime = timeAll - System.currentTimeMillis()
                    for (i in github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIconLoreTime) {
                        array.add(
                            i.replace(
                                "%time%",
                                TimeUtil.getInstance()
                                    .convertMillisToString(
                                        remainingTime,
                                        github.genesyspl.cardinal.configs.MainConfig.getInstance().kitsUseShortTime
                                    )
                            )
                        )
                    }
                    inv.setItem(
                        to1,
                        ItemUtil.getInstance()
                            .item(
                                Material.ARROW,
                                github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIconNotCatch,
                                array,
                                true
                            )
                    )
                    continue
                }
                inv.setItem(
                    to1,
                    ItemUtil.getInstance().item(
                        Material.ARROW,
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIconNotCatch,
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIconLoreNotPerm,
                        true
                    )
                )
                continue
            }
            inv.setItem(
                to1,
                ItemUtil.getInstance().item(DataManager.getInstance().material["glass"]!!, "§eKIT", true)
            )
        }
        p.openInventory(inv)
    }
}