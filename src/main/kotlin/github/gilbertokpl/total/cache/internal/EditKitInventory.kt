package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

internal object EditKitInventory {

    private const val EDIT_KIT_INVENTORY_SIZE = 27
    private const val EDIT_KIT_ITEMS_INVENTORY_SIZE = 36

    private val CHEST_ITEM = ItemUtil.item(
        Material.CHEST,
        LangConfig.kitsEditKitInventoryItemsName,
        LangConfig.kitsEditKitInventoryItemsLore
    )

    private val CLOCK_ITEM = ItemUtil.item(
        MaterialUtil["clock"] ?: Material.WATCH,
        LangConfig.kitsEditKitInventoryTimeName,
        LangConfig.kitsEditKitInventoryTimeLore
    )

    private val BOOK_ITEM = ItemUtil.item(
        Material.BOOK,
        LangConfig.kitsEditKitInventoryNameName,
        LangConfig.kitsEditKitInventoryNameLore
    )

    private val FEATHER_ITEM = ItemUtil.item(
        MaterialUtil["feather"] ?: Material.FEATHER,
        LangConfig.kitsEditKitInventoryWeightName,
        LangConfig.kitsEditKitInventoryWeightLore
    )

    private val GLASS_ITEM = ItemUtil.item(MaterialUtil["glass"] ?: Material.GLASS, "", true)

    fun setup() {
        for (slot in 0 until EDIT_KIT_INVENTORY_SIZE) {
            DataManager.editKitItemCache[slot] = when (slot) {
                10 -> CHEST_ITEM
                12 -> CLOCK_ITEM
                14 -> BOOK_ITEM
                16 -> FEATHER_ITEM
                else -> GLASS_ITEM
            }
        }
    }

    fun editKitGui(p: Player, kit: String) {
        val inv: Inventory = github.gilbertokpl.total.TotalEssentials.instance.server
            .createInventory(null, EDIT_KIT_INVENTORY_SIZE, "§eEditKit $kit")
        DataManager.editKitItemCache.forEach { (slot, item) ->
            inv.setItem(slot, item)
        }
        p.openInventory(inv)
    }

    fun editKitGuiItems(p: Player, kit: String, items: List<ItemStack>) {
        val inv: Inventory = github.gilbertokpl.total.TotalEssentials.instance.server
            .createInventory(null, EDIT_KIT_ITEMS_INVENTORY_SIZE, kit)
        items.forEach { item ->
            inv.addItem(item)
        }
        p.openInventory(inv)
    }
}
