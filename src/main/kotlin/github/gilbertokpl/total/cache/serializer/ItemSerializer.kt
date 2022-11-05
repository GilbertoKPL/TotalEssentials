package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.external.cache.convert.SerializatorBase
import org.bukkit.inventory.ItemStack

internal class ItemSerializer : SerializatorBase<ArrayList<ItemStack>, String> {
    override fun convertToDatabase(hash: ArrayList<ItemStack>): String {
        return github.gilbertokpl.total.TotalEssentials.basePlugin.getInventory().serialize(hash)
    }

    override fun convertToCache(value: String): ArrayList<ItemStack> {
        return github.gilbertokpl.total.TotalEssentials.basePlugin.getInventory().deserialize(value)
    }
}