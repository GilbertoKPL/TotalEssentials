package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.lang.reflect.Field

internal object ItemUtil {

    fun pickupKit(p: Player, kit: String) {

        // check if player don't have permission
        if (!p.hasPermission("totalessentials.commands.kit.$kit")) {
            p.sendMessage(LangConfig.generalNotPerm)
            return
        }

        //get all time of kit
        val kitsCache = PlayerData.kitsCache[p] ?: return
        var timeAll = kitsCache[kit] ?: 0L

        timeAll += KitsData.kitTime[kit]!!

        // if time is remaining
        if (timeAll >= System.currentTimeMillis() && !p.hasPermission("totalessentials.bypass.kitcatch")) {
            val remainingTime = timeAll - System.currentTimeMillis()
            p.sendMessage(
                LangConfig.kitsGetMessage.replace(
                    "%time%",
                    TotalEssentials.getCore().getTime().convertMillisToString(
                        remainingTime,
                        MainConfig.kitsUseShortTime
                    )
                )
            )
            return
        }

        //send kit to player (sempre clona antes de entregar)
        val clonedItems = KitsData.kitItems[kit]!!.map { it.clone() }.toMutableList()

        if (giveKit(
                p,
                clonedItems,
                MainConfig.kitsEquipArmorInCatch,
                MainConfig.kitsDropItemsInCatch
            )
        ) {
            PlayerData.kitsCache[p] = hashMapOf(kit to System.currentTimeMillis())
            p.sendMessage(
                LangConfig.kitsGetSuccess.replace(
                    "%kit%",
                    KitsData.kitFakeName[kit].let {
                        if (it.isNullOrEmpty()) kit else it
                    }
                )
            )
        }
    }

    fun giveKit(p: Player, items: MutableList<ItemStack>, armorAutoEquip: Boolean, drop: Boolean = false): Boolean {
        val inv = p.inventory
        val itemsInternal = ArrayList<ItemStack>()
        var inventorySpace = (0..35).count { inv.getItem(it) == null }

        val armor = ArrayList<String>()
        val itemsArmorInternal = HashMap<String, ItemStack>()

        //check if player has space in Armor contents
        if (armorAutoEquip) {
            fun helper(to: ItemStack?, name: String) {
                if (to == null) armor.add(name)
            }
            helper(inv.helmet, "HELMET")
            helper(inv.chestplate, "CHESTPLATE")
            helper(inv.leggings, "LEGGINGS")
            helper(inv.boots, "BOOTS")
        }

        //check if item is armor
        for (i in items) {
            val itemClone = i.clone() // <- aqui garante cópia
            if (armorAutoEquip) {
                var bolArmor = false
                val split = itemClone.type.name.split("_")
                split.forEach {
                    if ((it.contains("HELMET") ||
                                it.contains("CHESTPLATE") ||
                                it.contains("LEGGINGS") ||
                                it.contains("BOOTS")) && armor.contains(it)
                    ) {
                        armor.remove(it)
                        itemsArmorInternal[it] = itemClone
                        bolArmor = true
                    }
                }
                if (bolArmor) continue
            }
            itemsInternal.add(itemClone)
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
            if (inventorySpace >= itemsInternal.size) {
                for (i in itemsInternal) {
                    p.inventory.addItem(i)
                }
            } else {
                p.sendMessage(
                    LangConfig.kitsGetNoSpace.replace(
                        "%slots%",
                        (itemsInternal.size - inventorySpace).toString()
                    )
                )
                return false
            }
        }

        if (armorAutoEquip) {
            for (i in itemsArmorInternal) {
                when (i.key) {
                    "HELMET" -> inv.helmet = i.value
                    "CHESTPLATE" -> inv.chestplate = i.value
                    "LEGGINGS" -> inv.leggings = i.value
                    "BOOTS" -> inv.boots = i.value
                }
            }
        }
        return true
    }

    var usage = false
    fun setDisplayName(meta: ItemMeta?, name: String) {
        if (!usage) {
            try {
                meta?.setDisplayName(name)
                return
            } catch (e: NoSuchMethodError) {
                usage = true
            }
        }
        val field: Field = ItemMeta::class.java.getDeclaredField("displayName")
        field.isAccessible = true
        field.set(meta, name)
    }

    fun item(material: Material, name: String, lore: List<String>, effect: Boolean = false): ItemStack {
        val item = ItemStack(material)
        if (effect) {
            try {
                val en = EnchantUtil["luck"]
                if (en != null) {
                    item.addUnsafeEnchantment(en, 1)
                }
            } catch (ignored: NoSuchFieldError) {
            }
        }
        val meta = item.itemMeta
        meta?.lore = lore
        try {
            setDisplayName(meta, name)
        }
        catch (e : NoSuchMethodError) {
            meta?.setDisplayName(name)
        }
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
                val en = EnchantUtil["luck"]
                if (en != null) {
                    item.addUnsafeEnchantment(en, 1)
                }
            } catch (ignored: NoSuchFieldError) {
            }
        }
        val meta = item.itemMeta
        try {
            setDisplayName(meta, name)
        }
        catch (e : NoSuchMethodError) {
            meta?.setDisplayName(name)
        }
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
