package github.gilbertokpl.core.utils

import github.gilbertokpl.core.serializator.ItemSerializer
import org.bukkit.inventory.ItemStack

class InventoryUtil {
    private val itemInstance = ItemSerializer()

    fun serialize(items: ArrayList<ItemStack>): String {
        return itemInstance.serialize(items)
    }

    fun serialize(items: ItemStack): String {
        return itemInstance.serialize(items)
    }

    fun deserialize(data: String): ArrayList<ItemStack> {
        return itemInstance.deserialize(data)
    }
}