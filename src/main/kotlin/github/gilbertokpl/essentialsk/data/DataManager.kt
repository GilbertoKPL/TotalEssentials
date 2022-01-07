package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.data.`object`.KitDataV2
import github.gilbertokpl.essentialsk.data.`object`.PlayerDataV2
import github.gilbertokpl.essentialsk.manager.IInstance
import net.dv8tion.jda.api.entities.TextChannel
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class DataManager {

    //PlayerData
    val playerCacheV2 = ConcurrentHashMap<String, PlayerDataV2>()

    //KitCache
    val kitCacheV2 = HashMap<String, KitDataV2>()

    //discord
    var discordChat: TextChannel? = null

    //spawn
    val spawnCache = HashMap<String, Location>(2)

    //warps
    val warpsCache = HashMap<String, Location>(40)

    //tpa
    val tpaHash = HashMap<Player, Player>()

    val tpAcceptHash = HashMap<Player, Int>()

    //in teleport
    val inTeleport = ArrayList<Player>()

    //editKitInv
    val editKitInventory = HashMap<Int, ItemStack>(50)

    //kitInv
    val kitGuiCache = HashMap<Int, Inventory>(10)

    //click kitInv
    val kitClickGuiCache = HashMap<Int, String>(40)

    //material helper
    val material = HashMap<String, Material>(10)

    //editKit
    val editKit = HashMap<Player, String>(10)

    val editKitChat = HashMap<Player, String>(10)

    companion object : IInstance<DataManager> {
        private val instance = createInstance()
        override fun createInstance(): DataManager = DataManager()
        override fun getInstance(): DataManager {
            return instance
        }
    }
}
