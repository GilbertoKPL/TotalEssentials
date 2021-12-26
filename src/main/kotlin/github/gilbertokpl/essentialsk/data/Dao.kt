package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.manager.IInstance
import net.dv8tion.jda.api.entities.TextChannel
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Dao {

    //discord
    var discordChat: TextChannel? = null

    //spawn
    val spawnCache = HashMap<String, Location>(2)

    //back Command
    val backLocation = HashMap<Player, Location>(40)

    //warps
    val warpsCache = HashMap<String, Location>(40)

    //kits
    val kitsCache = HashMap<String, KitData.KitDataInternal>(40)

    //player
    val playerCache = HashMap<String, PlayerData.InternalPlayerData>(100)

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

    companion object : IInstance<Dao> {
        private val instance = createInstance()
        override fun createInstance(): Dao = Dao()
        override fun getInstance(): Dao {
            return instance
        }
    }
}