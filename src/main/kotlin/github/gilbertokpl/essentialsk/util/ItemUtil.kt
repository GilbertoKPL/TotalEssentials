package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.KitCache.setKitTime
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

internal object ItemUtil {

    fun pickupKit(p: Player, kit: String) {

        // check if player don't have permission
        if (!p.hasPermission("essentialsk.commands.kit.$kit")) {
            p.sendMessage(GeneralLang.generalNotPerm)
            return
        }

        //load caches
        val kitCache = KitData[kit] ?: return
        val playerCache = PlayerData[p] ?: return

        //get all time of kit
        var timeAll = playerCache.kitsCache[kit] ?: 0L
        timeAll += kitCache.timeCache

        // if time is remaining
        if (timeAll >= System.currentTimeMillis() && !p.hasPermission("essentialsk.bypass.kitcatch")) {
            val remainingTime = timeAll - System.currentTimeMillis()
            p.sendMessage(
                GeneralLang.kitsCatchMessage.replace(
                    "%time%",
                    TimeUtil
                        .convertMillisToString(remainingTime, MainConfig.kitsUseShortTime)
                )
            )
            return
        }

        //send kit to player
        if (giveKit(
                p,
                kitCache.itemsCache,
                MainConfig.kitsEquipArmorInCatch,
                MainConfig.kitsDropItemsInCatch
            )
        ) {
            playerCache.setKitTime(kit, System.currentTimeMillis())
            p.sendMessage(GeneralLang.kitsCatchSuccess.replace("%kit%", kitCache.fakeNameCache))
        }
    }

    fun giveKit(p: Player, items: List<ItemStack>, armorAutoEquip: Boolean, drop: Boolean = false): Boolean {
        //bug armored check error
        val inv = p.inventory

        val itemsInternal = ArrayList<ItemStack>()

        var inventorySpace = 0

        for (i in 0..36) {
            if (inv.getItem(i) == null) {
                inventorySpace += 1
            }
        }

        val armor = ArrayList<String>()

        val itemsArmorInternal = HashMap<String, ItemStack>()

        //check if player has space in Armor contents
        if (armorAutoEquip) {
            fun helper(to: ItemStack?, name: String) {
                if (to == null) {
                    armor.add(name)
                }
            }
            helper(inv.helmet, "HELMET")
            helper(inv.chestplate, "CHESTPLATE")
            helper(inv.leggings, "LEGGINGS")
            helper(inv.boots, "BOOTS")
        }

        //check if item is armor
        for (i in items) {
            if (armorAutoEquip) {
                var bolArmor = false
                val split = i.type.name.split("_")
                split.forEach {
                    if ((it.contains("HELMET") ||
                                it.contains("CHESTPLATE") ||
                                it.contains("LEGGINGS") ||
                                it.contains("BOOTS")) && armor.contains(it)
                    ) {
                        armor.remove(it)
                        itemsArmorInternal[it] = i
                        bolArmor = true
                    }
                }
                if (bolArmor) continue
            }
            itemsInternal.add(i)
        }

        //drop itens if full
        if (drop) {
            for (i in itemsInternal) {
                if (inventorySpace > 0) {
                    p.inventory.addItem(i)
                    inventorySpace -= 1
                    continue
                }
                p.world.dropItem(p.location, i)
            }
        } else {
            //check if inventory is full and add item
            if (inventorySpace >= itemsInternal.size) {
                for (i in itemsInternal) {
                    p.inventory.addItem(i)
                }
            } else {
                //send message if inventory is full
                p.sendMessage(
                    GeneralLang.kitsCatchNoSpace.replace(
                        "%slots%",
                        (itemsInternal.size - inventorySpace).toString()
                    )
                )
                return false
            }
        }

        if (armorAutoEquip) {
            for (i in itemsArmorInternal) {
                if (i.key == "HELMET") {
                    inv.helmet = i.value
                    continue
                }
                if (i.key == "CHESTPLATE") {
                    inv.chestplate = i.value
                    continue
                }
                if (i.key == "LEGGINGS") {
                    inv.leggings = i.value
                    continue
                }
                if (i.key == "BOOTS") {
                    inv.boots = i.value
                    continue
                }
            }
        }
        return true
    }

    fun item(material: Material, name: String, lore: List<String>, effect: Boolean = false): ItemStack {
        val item = ItemStack(material)
        if (effect) {
            try {
                item.addUnsafeEnchantment(Enchantment.LUCK, 1)
            } catch (ignored: NoSuchFieldError) {
            }
        }
        val meta = item.itemMeta
        meta?.lore = lore
        meta?.setDisplayName(name)
        if (effect) {
            try {
                meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            } catch (ignored: NoClassDefFoundError) {
            }
        }
        item.itemMeta = meta
        return item
    }

    fun item(material: Material, name: String, effect: Boolean = false): ItemStack {
        val item = ItemStack(material)
        if (effect) {
            try {
                item.addUnsafeEnchantment(Enchantment.LUCK, 1)
            } catch (ignored: NoSuchFieldError) {
            }
        }
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        if (effect) {
            try {
                meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            } catch (ignored: NoClassDefFoundError) {
            }
        }
        item.itemMeta = meta
        return item
    }
}
