package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.cache.internal.DataManager.kitInventoryCache
import github.gilbertokpl.total.cache.internal.DataManager.kitItemCache
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.local.KitsData.kitFakeName
import github.gilbertokpl.total.cache.local.KitsData.kitWeight
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

internal object KitGuiInventory {

    private val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§eKIT", true)

    fun setup() {
        kitItemCache.clear()
        kitInventoryCache.clear()
        var currentPage = 1
        var currentSlot = 0
        var inventory = createKitsInventory(currentPage)

        val sortedKits = kitWeight.getMap().toList().sortedBy { (_, value) -> value }.reversed().toMap()

        for (kit in sortedKits) {
            val kitName = kitFakeName[kit.key]?.takeIf { it.isNotEmpty() } ?: kit.key

            val itemName = LangConfig.kitsInventoryItemsName.replace("%kitrealname%", kitName)
            val item = KitsData.kitItems[kit.key]?.getOrNull(0) ?: ItemStack(Material.CHEST)

            val meta = item.itemMeta
            item.amount = 1
            meta.displayName = itemName
            meta.lore = LangConfig.kitsInventoryItemsLore.map { it.replace("%realname%", kit.key) }
            item.itemMeta = meta

            val cacheValue = currentSlot + 1 + (27 * (currentPage - 1))
            kitItemCache[cacheValue] = kit.key

            inventory.setItem(currentSlot, item)
            currentSlot++
            if (currentSlot == 27) {
                inventory.setItem(27, createBackItem(currentPage))
                for (i in 28..34) {
                    inventory.setItem(i, GLASS_MATERIAL)
                }
                inventory.setItem(35, createNextItem(currentPage, sortedKits.size))
                kitInventoryCache[currentPage] = inventory
                currentPage++
                currentSlot = 0
                inventory = createKitsInventory(currentPage)
            }
        }

        if (currentSlot > 0) {
            inventory.setItem(27, if (currentPage > 1) createBackItem(currentPage) else GLASS_MATERIAL)
            for (i in 28..35) {
                inventory.setItem(i, GLASS_MATERIAL)
            }
            kitInventoryCache[currentPage] = inventory
        }
    }

    fun openKitInventory(kit: String, guiNumber: String, player: Player) {
        val inventory = github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 45, "§eKit $kit $guiNumber")
        var timeAll = PlayerData.kitsCache[player]?.get(kit) ?: 0L
        timeAll += KitsData.kitTime[kit] ?: 0L
        KitsData.kitItems[kit]?.forEach { inventory.addItem(it) }
        for (i in 36..44) {
            when (i) {
                36 -> setInventoryItem(i, Material.HOPPER, LangConfig.kitsInventoryIconBackName, inventory)
                40 -> {
                    if (player.hasPermission("totalessentials.commands.editkit")) {
                        setInventoryItem(i, Material.CHEST, LangConfig.kitsInventoryIconEditKitName, inventory)
                    } else {
                        GLASS_MATERIAL
                    }
                }
                44 -> {
                    if (player.hasPermission("totalessentials.commands.kit.$kit")) {
                        if (timeAll <= System.currentTimeMillis() || timeAll == 0L || player.hasPermission("totalessentials.bypass.kitcatch")) {
                            setInventoryItem(i, Material.ARROW, LangConfig.kitsGetIcon, inventory)
                        } else {
                            val array = arrayOfNulls<String>(LangConfig.kitsGetIconLoreTime.size)
                            val remainingTime = timeAll - System.currentTimeMillis()
                            for (j in LangConfig.kitsGetIconLoreTime.indices) {
                                array[j] = LangConfig.kitsGetIconLoreTime[j].replace("%time%", github.gilbertokpl.total.TotalEssentials.basePlugin.getTime().convertMillisToString(remainingTime, MainConfig.kitsUseShortTime))
                            }
                            setInventoryItem(i, Material.ARROW, LangConfig.kitsGetIconNotCatch, array.toList().requireNoNulls(), inventory)
                        }
                    } else {
                        setInventoryItem(i, Material.ARROW, LangConfig.kitsGetIconNotCatch, LangConfig.kitsGetIconLoreNotPerm, inventory)
                    }
                }
                else -> inventory.setItem(i, GLASS_MATERIAL)
            }
        }
        player.openInventory(inventory)
    }

    private fun setInventoryItem(slot: Int, material: Material, name: String, inventory: Inventory) {
        inventory.setItem(slot, ItemUtil.item(material, name, true))
    }

    private fun setInventoryItem(slot: Int, material: Material, name: String, lore: List<String>, inventory: Inventory) {
        inventory.setItem(slot, ItemUtil.item(material, name, lore, true))
    }

    private fun createKitsInventory(page: Int): Inventory {
        return github.gilbertokpl.total.TotalEssentials.instance.server.createInventory(null, 36, "§eKits $page")
    }

    private fun createBackItem(currentPage: Int): ItemStack {
        return if (currentPage > 1) {
            ItemUtil.item(Material.HOPPER, LangConfig.kitsInventoryIconBackName, true)
        } else {
            GLASS_MATERIAL
        }
    }

    private fun createNextItem(currentPage: Int, totalKits: Int): ItemStack {
        return if (currentPage * 27 < totalKits) {
            ItemUtil.item(Material.ARROW, LangConfig.kitsInventoryIconNextName, true)
        } else {
            GLASS_MATERIAL
        }
    }
}
