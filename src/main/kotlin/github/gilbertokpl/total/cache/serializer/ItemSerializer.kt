package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.external.cache.convert.SerializerBase
import github.gilbertokpl.total.TotalEssentialsJava
import org.bukkit.inventory.ItemStack

internal class ItemSerializer : SerializerBase<ArrayList<ItemStack>, String> {
    override fun convertToDatabase(hash: ArrayList<ItemStack>): String {
        return TotalEssentialsJava.getBasePlugin().getInventory().serialize(hash)
    }

    override fun convertToCache(value: String): ArrayList<ItemStack> {
        return TotalEssentialsJava.getBasePlugin().getInventory().deserialize(value)
    }
}