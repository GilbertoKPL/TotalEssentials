package github.gilbertokpl.essentialsk.inventory

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EditKitInventory {
    fun setup() {
        for (inventory in 0..26) {
            if (inventory == 10) {
                DataManager.editKitInventory[inventory] =
                    ItemUtil.item(
                        Material.CHEST,
                        GeneralLang.kitsEditKitInventoryItemsName,
                        GeneralLang.kitsEditKitInventoryItemsLore
                    )
                continue
            }
            if (inventory == 12) {
                DataManager.editKitInventory[inventory] =
                    ItemUtil.item(
                        DataManager.material["clock"]!!,
                        GeneralLang.kitsEditKitInventoryTimeName,
                        GeneralLang.kitsEditKitInventoryTimeLore
                    )
                continue
            }
            if (inventory == 14) {
                DataManager.editKitInventory[inventory] =
                    ItemUtil.item(
                        Material.BOOK,
                        GeneralLang.kitsEditKitInventoryNameName,
                        GeneralLang.kitsEditKitInventoryNameLore
                    )
                continue
            }
            if (inventory == 16) {
                DataManager.editKitInventory[inventory] =
                    ItemUtil.item(
                        DataManager.material["feather"]!!,
                        GeneralLang.kitsEditKitInventoryWeightName,
                        GeneralLang.kitsEditKitInventoryWeightLore
                    )
                continue
            }
            DataManager.editKitInventory[inventory] =
                ItemUtil.item(DataManager.material["glass"]!!, "", true)
        }
    }

    fun editKitGui(p: Player, kit: String) {
        val inv = EssentialsK.instance.server.createInventory(null, 27, "§eEditKit $kit")
        for (i in DataManager.editKitInventory) {
            inv.setItem(i.key, i.value)
        }
        p.openInventory(inv)
    }

    fun editKitGuiItems(p: Player, kit: String, items: List<ItemStack>) {
        val inv = EssentialsK.instance.server.createInventory(null, 36, kit)
        items.forEach {
            inv.addItem(it)
        }
        p.openInventory(inv)
    }
}
