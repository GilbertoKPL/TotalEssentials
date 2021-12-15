package github.gilbertokpl.essentialsk.inventory

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EditKitInventory {
    fun setup() {
        for (inventory in 0..26) {
            if (inventory == 11) {
                Dao.getInstance().editKitInventory[inventory] =
                    ItemUtil.getInstance().item(
                        Material.CHEST,
                        GeneralLang.getInstance().kitsEditKitInventoryItemsName,
                        GeneralLang.getInstance().kitsEditKitInventoryItemsLore
                    )
                continue
            }
            if (inventory == 13) {
                Dao.getInstance().editKitInventory[inventory] =
                    ItemUtil.getInstance().item(
                        Dao.getInstance().material["clock"]!!,
                        GeneralLang.getInstance().kitsEditKitInventoryTimeName,
                        GeneralLang.getInstance().kitsEditKitInventoryTimeLore
                    )
                continue
            }
            if (inventory == 15) {
                Dao.getInstance().editKitInventory[inventory] =
                    ItemUtil.getInstance().item(
                        Dao.getInstance().material["feather"]!!,
                        GeneralLang.getInstance().kitsEditKitInventoryNameName,
                        GeneralLang.getInstance().kitsEditKitInventoryNameLore
                    )
                continue
            }
            Dao.getInstance().editKitInventory[inventory] =
                ItemUtil.getInstance().item(Dao.getInstance().material["glass"]!!, "", true)
        }
    }

    fun editKitGui(p: Player, kit: String) {
        val inv = EssentialsK.instance.server.createInventory(null, 27, "§eEditKit $kit")
        for (i in Dao.getInstance().editKitInventory) {
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