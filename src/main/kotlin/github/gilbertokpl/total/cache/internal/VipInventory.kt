package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil

object VipInventory {
    private val GLASS_MATERIAL = ItemUtil.item(MaterialUtil["glass"]!!, "§eVIP", true)

    fun setup() {
        DataManager.VipInventory.clear()

        for (inventory in 0..26) {


            DataManager.editKitInventory[inventory] = GLASS_MATERIAL
        }

    }


}