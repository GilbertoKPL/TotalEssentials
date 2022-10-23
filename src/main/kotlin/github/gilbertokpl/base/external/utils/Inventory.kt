package github.gilbertokpl.base.external.utils

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.internal.inventory.serializator.ItemSerializator
import org.bukkit.inventory.ItemStack

class Inventory(lf: BasePlugin) {
    private val iteminstance = ItemSerializator()

    fun serialize(items: ArrayList<ItemStack>): String {
        return iteminstance.serialize(items)
    }

    fun deserialize(data: String): ArrayList<ItemStack> {
        return iteminstance.deserialize(data)
    }
}