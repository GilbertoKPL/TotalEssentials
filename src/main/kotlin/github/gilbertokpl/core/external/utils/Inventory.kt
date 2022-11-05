package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.inventory.serializator.ItemSerializator
import org.bukkit.inventory.ItemStack

class Inventory(lf: CorePlugin) {
    private val iteminstance = ItemSerializator()

    fun serialize(items: ArrayList<ItemStack>): String {
        return iteminstance.serialize(items)
    }

    fun deserialize(data: String): ArrayList<ItemStack> {
        return iteminstance.deserialize(data)
    }
}