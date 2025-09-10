package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.cache.interfaces.ICacheSerializer
import github.gilbertokpl.total.TotalEssentials
import org.bukkit.inventory.ItemStack

internal class ItemSerializer : ICacheSerializer<ArrayList<ItemStack>, String> {
    override fun convertToDatabase(hash: ArrayList<ItemStack>): String {
        return TotalEssentials.getCore().getInventory().serialize(hash)
    }

    override fun convertToCache(value: String): ArrayList<ItemStack> {
        return TotalEssentials.getCore().getInventory().deserialize(value)
    }
}