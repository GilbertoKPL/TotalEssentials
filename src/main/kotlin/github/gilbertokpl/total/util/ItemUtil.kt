package github.gilbertokpl.total.util

import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
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
                    github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getTime().convertMillisToString(
                        remainingTime,
                        MainConfig.kitsUseShortTime
                    )
                )
            )
            return
        }

        //send kit to player
        if (giveKit(
                p,
                KitsData.kitItems[kit]!!,
                MainConfig.kitsEquipArmorInCatch,
                MainConfig.kitsDropItemsInCatch
            )
        ) {
            PlayerData.kitsCache[p] = hashMapOf(kit to System.currentTimeMillis())
            p.sendMessage(
                LangConfig.kitsGetSuccess.replace(
                    "%kit%",
                    KitsData.kitFakeName[kit].let {
                        if (it == "" || it == null) kit else it
                    }
                )
            )
        }
    }

    fun giveKit(p: Player, items: MutableList<ItemStack>, armorAutoEquip: Boolean, drop: Boolean = false): Boolean {
        //bug armored check error
        val inv = p.inventory

        val itemsInternal = ArrayList<ItemStack>()

        var inventorySpace = 0

        for (i in 0..35) {
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
                item.addUnsafeEnchantment(Enchantment.LUCK, 1)
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
                item.addUnsafeEnchantment(Enchantment.LUCK, 1)
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
