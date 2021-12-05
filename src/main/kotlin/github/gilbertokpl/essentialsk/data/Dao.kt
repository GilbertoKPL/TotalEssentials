package github.gilbertokpl.essentialsk.data

import com.github.benmanes.caffeine.cache.Caffeine
import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Dao {
    //kits
    val kitsCache =
        Caffeine.newBuilder().initialCapacity(20).maximumSize(500).buildAsync<String, KitData.KitDataInternal>()

    //player
    val playerCache =
        Caffeine.newBuilder().initialCapacity(20).maximumSize(500).buildAsync<String, PlayerData.InternalPlayerData>()

    //editKitInv
    val editKitInventory = HashMap<Int, ItemStack>(10)

    //kitInv
    val kitGuiCache = HashMap<Int, Inventory>(10)

    //click kitInv
    val kitClickGuiCache = Caffeine.newBuilder().initialCapacity(20).maximumSize(500).build<Int, String>()

    //material helper
    val material = HashMap<String, Material>(50)

    companion object : IInstance<Dao> {
        private val instance = createInstance()
        override fun createInstance(): Dao = Dao()
        override fun getInstance(): Dao {
            return instance
        }
    }
}